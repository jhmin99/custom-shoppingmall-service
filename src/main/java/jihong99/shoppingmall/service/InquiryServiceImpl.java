package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.inquiry.ChildResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.PatchResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.ResponseRequestDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryDetailsResponseDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryResponseDto;
import jihong99.shoppingmall.entity.Inquiry;
import jihong99.shoppingmall.entity.InquiryResponse;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import jihong99.shoppingmall.exception.HasRelatedEntitiesException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.InquiryRepository;
import jihong99.shoppingmall.repository.InquiryResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements IInquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryResponseRepository inquiryResponseRepository;

    /**
     * Adds a response to a specific inquiry.
     *
     * @param inquiryId The ID of the inquiry to respond to.
     * @param responseRequestDto The DTO containing the response content.
     * @throws NotFoundException if the inquiry is not found.
     */
    @Override
    @Transactional
    public void respondToInquiry(Long inquiryId, ResponseRequestDto responseRequestDto) {
        Inquiry inquiry = findInquiryOrThrow(inquiryId);
        InquiryResponse response = InquiryResponse.createResponse(inquiry, responseRequestDto.getContent());
        inquiryResponseRepository.save(response);
    }

    /**
     * Edits an existing inquiry response.
     *
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the response to edit.
     * @param patchResponseRequestDto The DTO containing the updated response content.
     * @throws NotFoundException if the inquiry or response is not found.
     * @throws HasRelatedEntitiesException if the response has child responses and cannot be edited.
     */
    @Override
    @Transactional
    public void editInquiryResponse(Long inquiryId, Long responseId, PatchResponseRequestDto patchResponseRequestDto) {
        findInquiryOrThrow(inquiryId);
        InquiryResponse response = findInquiryResponseOrThrow(responseId);

        if (!response.getChildResponses().isEmpty()) {
            throw new HasRelatedEntitiesException(MESSAGE_409_RelationConflict);
        }

        response.updateContent(patchResponseRequestDto.getContent());
        inquiryResponseRepository.save(response);
    }

    /**
     * Deletes a specific inquiry response.
     *
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the response to delete.
     * @throws NotFoundException if the inquiry or response is not found.
     * @throws HasRelatedEntitiesException if the response has child responses and cannot be deleted.
     */
    @Override
    @Transactional
    public void deleteInquiryResponse(Long inquiryId, Long responseId) {
        findInquiryOrThrow(inquiryId);
        InquiryResponse response = findInquiryResponseOrThrow(responseId);

        if (!response.getChildResponses().isEmpty()) {
            throw new HasRelatedEntitiesException(MESSAGE_409_RelationConflict);
        }

        inquiryResponseRepository.delete(response);
    }

    /**
     * Retrieves a paginated list of inquiries filtered by type and status.
     *
     * @param type The type of inquiry (e.g., ITEM, CUSTOMER).
     * @param status The status of the inquiry (e.g., RESOLVED, UNRESOLVED).
     * @param pageable The pagination information.
     * @return A paginated list of InquiryResponseDto objects.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResponseDto> getAllInquiries(InquiryType type, InquiryStatus status, Pageable pageable) {
        return inquiryRepository.findAllByTypeAndStatus(type, status, pageable)
                .map(inquiry -> {
                    String itemName = inquiry.getType() == InquiryType.ITEM && inquiry.getItem() != null
                            ? inquiry.getItem().getName()
                            : null;

                    return getInquiryResponseDto(inquiry, itemName);
                });
    }

    /**
     * Retrieves the details of a specific inquiry, including its responses.
     *
     * @param inquiryId The ID of the inquiry.
     * @return The details of the inquiry as an InquiryDetailsResponseDto.
     * @throws NotFoundException if the inquiry is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public InquiryDetailsResponseDto getInquiryDetails(Long inquiryId) {
        Inquiry inquiry = findInquiryOrThrow(inquiryId);

        List<InquiryDetailsResponseDto.ResponseDto> responses = mapInquiryResponsesToDtoList(inquiry);

        String itemName = inquiry.getItem() != null ? inquiry.getItem().getName() : null;
        return buildInquiryDetailsResponseDto(inquiry, itemName, responses);
    }

    /**
     * Adds a child response to a specific parent response within an inquiry.
     *
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the parent response.
     * @param responseRequestDto The DTO containing the child response content.
     * @throws NotFoundException if the inquiry or parent response is not found.
     */
    @Override
    @Transactional
    public void respondToParentResponse(Long inquiryId, Long responseId, ChildResponseRequestDto responseRequestDto) {
        findInquiryOrThrow(inquiryId);
        InquiryResponse parentResponse = findInquiryResponseOrThrow(responseId);

        InquiryResponse childResponse = InquiryResponse.createChildResponse(parentResponse, responseRequestDto.getContent());
        inquiryResponseRepository.save(childResponse);
    }




    private List<InquiryDetailsResponseDto.ResponseDto> mapInquiryResponsesToDtoList(Inquiry inquiry) {
        return inquiry.getResponses().stream()
                .map(this::mapInquiryResponseToDto)
                .collect(Collectors.toList());
    }

    private static InquiryDetailsResponseDto buildInquiryDetailsResponseDto(Inquiry inquiry, String itemName, List<InquiryDetailsResponseDto.ResponseDto> responses) {
        return InquiryDetailsResponseDto.of(
                inquiry.getId(),
                inquiry.getType(),
                itemName,
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getStatus(),
                inquiry.getRegistrationDate(),
                responses
        );
    }

    private Inquiry findInquiryOrThrow(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_InquiryNotFound));
    }

    private InquiryResponse findInquiryResponseOrThrow(Long responseId) {
        return inquiryResponseRepository.findById(responseId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ResponseNotFound));
    }

    private InquiryDetailsResponseDto.ResponseDto mapInquiryResponseToDto(InquiryResponse response) {
        List<InquiryDetailsResponseDto.ResponseDto> childResponses = mapInquiryResponsesToDtoList(response);
        return getResponseDto(response, childResponses);
    }



    private List<InquiryDetailsResponseDto.ResponseDto> mapInquiryResponsesToDtoList(InquiryResponse response) {
        return getResponseDtoList(response);
    }

    private List<InquiryDetailsResponseDto.ResponseDto> getResponseDtoList(InquiryResponse response) {
        return response.getChildResponses() != null ?
                response.getChildResponses().stream()
                        .map(this::mapInquiryResponseToDto)
                        .collect(Collectors.toList())
                : Collections.emptyList();
    }

    private static InquiryResponseDto getInquiryResponseDto(Inquiry inquiry, String itemName) {
        return InquiryResponseDto.of(
                inquiry.getId(),
                itemName,
                inquiry.getTitle(),
                inquiry.getType(),
                inquiry.getStatus()
        );
    }
    private static InquiryDetailsResponseDto.ResponseDto getResponseDto(InquiryResponse response, List<InquiryDetailsResponseDto.ResponseDto> childResponses) {
        return InquiryDetailsResponseDto.ResponseDto.of(
                response.getId(),
                response.getContent(),
                response.getRegistrationDate(),
                childResponses
        );
    }
}
