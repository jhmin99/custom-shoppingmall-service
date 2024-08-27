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
        Inquiry inquiry = validateInquiriesExist(inquiryId);
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
        validateInquiriesExist(inquiryId);
        InquiryResponse response = validateInquiryResponseExist(responseId);

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
        validateInquiriesExist(inquiryId);
        InquiryResponse response = validateInquiryResponseExist(responseId);

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

                    return InquiryResponseDto.of(
                            inquiry.getId(),
                            itemName,
                            inquiry.getTitle(),
                            inquiry.getType(),
                            inquiry.getStatus()
                    );
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
        Inquiry inquiry = validateInquiriesExist(inquiryId);

        List<InquiryDetailsResponseDto.ResponseDto> responses = inquiry.getResponses().stream()
                .map(this::convertInquiryResponseToDto)
                .collect(Collectors.toList());

        String itemName = inquiry.getItem() != null ? inquiry.getItem().getName() : null;

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
        validateInquiriesExist(inquiryId);
        InquiryResponse parentResponse = validateInquiryResponseExist(responseId);

        InquiryResponse childResponse = InquiryResponse.createChildResponse(parentResponse, responseRequestDto.getContent());
        inquiryResponseRepository.save(childResponse);
    }

    /**
     * Validates that an inquiry with the given ID exists.
     *
     * @param inquiryId The ID of the inquiry to validate.
     * @return The Inquiry entity if found.
     * @throws NotFoundException if the inquiry is not found.
     */
    private Inquiry validateInquiriesExist(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_InquiryNotFound));
    }

    /**
     * Validates that an inquiry response with the given ID exists.
     *
     * @param responseId The ID of the response to validate.
     * @return The InquiryResponse entity if found.
     * @throws NotFoundException if the response is not found.
     */
    private InquiryResponse validateInquiryResponseExist(Long responseId) {
        return inquiryResponseRepository.findById(responseId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ResponseNotFound));
    }

    /**
     * Converts an InquiryResponse entity to an InquiryDetailsResponseDto.ResponseDto.
     *
     * @param response The InquiryResponse entity to convert.
     * @return The converted InquiryDetailsResponseDto.ResponseDto.
     */
    private InquiryDetailsResponseDto.ResponseDto convertInquiryResponseToDto(InquiryResponse response) {
        List<InquiryDetailsResponseDto.ResponseDto> childResponses = response.getChildResponses() != null ?
                response.getChildResponses().stream()
                        .map(this::convertInquiryResponseToDto)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return InquiryDetailsResponseDto.ResponseDto.of(
                response.getId(),
                response.getContent(),
                response.getRegistrationDate(),
                childResponses
        );
    }
}
