package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.inquiry.ChildResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.InquiryRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.PatchResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.ResponseRequestDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryDetailsResponseDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryResponseDto;
import jihong99.shoppingmall.entity.Inquiry;
import jihong99.shoppingmall.entity.InquiryResponse;
import jihong99.shoppingmall.entity.Item;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import jihong99.shoppingmall.exception.HasRelatedEntitiesException;
import jihong99.shoppingmall.exception.InvalidOperationException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.InquiryRepository;
import jihong99.shoppingmall.repository.InquiryResponseRepository;
import jihong99.shoppingmall.repository.ItemRepository;
import jihong99.shoppingmall.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
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
     * This method first checks if the response belongs to the given inquiry, and then ensures
     * that the response has no child responses before updating the content.
     *
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the response to edit.
     * @param patchResponseRequestDto The DTO containing the updated response content.
     * @throws NotFoundException if the inquiry or response is not found.
     * @throws HasRelatedEntitiesException if the response has child responses and cannot be edited.
     * @throws InvalidOperationException if the response does not belong to the inquiry.
     */
    @Override
    @Transactional
    public void editInquiryResponse(Long inquiryId, Long responseId, PatchResponseRequestDto patchResponseRequestDto) {
        findInquiryOrThrow(inquiryId);
        InquiryResponse response = findInquiryResponseOrThrow(responseId);

        validateInquiryResponseBelongsToInquiry(response, inquiryId);

        validateResponseHasNoChildResponses(response);

        response.updateContent(patchResponseRequestDto.getContent());
        inquiryResponseRepository.save(response);
    }

    /**
     * Deletes a specific inquiry response.
     * This method checks if the response belongs to the given inquiry, and then ensures
     * that the response has no child responses before deleting it.
     *
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the response to delete.
     * @throws NotFoundException if the inquiry or response is not found.
     * @throws HasRelatedEntitiesException if the response has child responses and cannot be deleted.
     * @throws InvalidOperationException if the response does not belong to the inquiry.
     */
    @Override
    @Transactional
    public void deleteInquiryResponse(Long inquiryId, Long responseId) {
        findInquiryOrThrow(inquiryId);
        InquiryResponse response = findInquiryResponseOrThrow(responseId);

        validateInquiryResponseBelongsToInquiry(response, inquiryId);

        validateResponseHasNoChildResponses(response);

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
     * This method ensures that the parent response belongs to the inquiry before adding the child response.
     *
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the parent response.
     * @param responseRequestDto The DTO containing the child response content.
     * @throws NotFoundException if the inquiry or parent response is not found.
     * @throws InvalidOperationException if the parent response does not belong to the inquiry.
     */
    @Override
    @Transactional
    public void respondToParentResponse(Long inquiryId, Long responseId, ChildResponseRequestDto responseRequestDto) {
        findInquiryOrThrow(inquiryId);
        InquiryResponse parentResponse = findInquiryResponseOrThrow(responseId);

        validateInquiryResponseBelongsToInquiry(parentResponse, inquiryId);

        InquiryResponse childResponse = InquiryResponse.createChildResponse(parentResponse, responseRequestDto.getContent());
        inquiryResponseRepository.save(childResponse);
    }

    /**
     * Submits an item-related inquiry by a user.
     *
     * @param userId The ID of the user submitting the inquiry.
     * @param itemId The ID of the item related to the inquiry.
     * @param inquiryRequestDto The DTO containing the inquiry details.
     * @throws NotFoundException if the user or item is not found.
     */
    @Override
    @Transactional
    public void submitItemInquiry(Long userId, Long itemId, InquiryRequestDto inquiryRequestDto) {
        Users user = findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);
        Inquiry inquiry = Inquiry.createItemInquiry(user, item, inquiryRequestDto.getTitle(), inquiryRequestDto.getContent());
        inquiryRepository.save(inquiry);
    }

    /**
     * Submits a general customer inquiry by a user.
     *
     * @param userId The ID of the user submitting the inquiry.
     * @param inquiryRequestDto The DTO containing the inquiry details.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    @Transactional
    public void submitCustomerInquiry(Long userId, InquiryRequestDto inquiryRequestDto) {
        Users user = findUserOrThrow(userId);
        Inquiry inquiry = Inquiry.createCustomerInquiry(user, inquiryRequestDto.getTitle(), inquiryRequestDto.getContent());
        inquiryRepository.save(inquiry);
    }

    /**
     * Retrieves a paginated list of all inquiries submitted by a user.
     *
     * @param userId The ID of the user.
     * @param pageable The pagination information.
     * @return A paginated list of InquiryResponseDto objects.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResponseDto> getAllUserInquiries(Long userId, Pageable pageable) {
        return inquiryRepository.findAllByUsersId(userId, pageable)
                .map(inquiry -> {
                    String itemName = inquiry.getType() == InquiryType.ITEM && inquiry.getItem() != null
                            ? inquiry.getItem().getName()
                            : null;

                    return getInquiryResponseDto(inquiry, itemName);
                });
    }

    /**
     * Retrieves the details of a specific inquiry submitted by a user.
     *
     * @param userId The ID of the user.
     * @param inquiryId The ID of the inquiry.
     * @return The details of the inquiry as an InquiryDetailsResponseDto.
     * @throws NotFoundException if the inquiry or user is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public InquiryDetailsResponseDto getUserInquiryDetails(Long userId, Long inquiryId) {
        findUserOrThrow(userId);
        Inquiry inquiry = findInquiryOrThrow(inquiryId, userId);

        List<InquiryDetailsResponseDto.ResponseDto> responses = mapInquiryResponsesToDtoList(inquiry);

        String itemName = inquiry.getItem() != null ? inquiry.getItem().getName() : null;
        return buildInquiryDetailsResponseDto(inquiry, itemName, responses);
    }

    /**
     * Deletes a specific inquiry submitted by a user.
     *
     * @param userId The ID of the user.
     * @param inquiryId The ID of the inquiry to delete.
     * @throws NotFoundException if the inquiry or user is not found.
     */
    @Override
    @Transactional
    public void deleteInquiry(Long userId, Long inquiryId) {
        findUserOrThrow(userId);
        Inquiry inquiry = findInquiryOrThrow(inquiryId, userId);
        inquiryRepository.delete(inquiry);
    }

    /**
     * Adds a child response to a specific parent response within a user's inquiry.
     *
     * @param userId The ID of the user.
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the parent response.
     * @param responseRequestDto The DTO containing the child response content.
     * @throws NotFoundException if the inquiry or response is not found.
     * @throws InvalidOperationException if the response does not belong to the inquiry.
     */
    @Override
    @Transactional
    public void respondToParentResponseForUser(Long userId, Long inquiryId, Long responseId, ChildResponseRequestDto responseRequestDto) {
        findInquiryOrThrow(inquiryId, userId);
        InquiryResponse parentResponse = findInquiryResponseOrThrow(responseId);

        validateInquiryResponseBelongsToInquiry(parentResponse, inquiryId);

        InquiryResponse childResponse = InquiryResponse.createChildResponse(parentResponse, responseRequestDto.getContent());
        inquiryResponseRepository.save(childResponse);
    }

    /**
     * Edits a response within a user's inquiry.
     * This method ensures that the response belongs to the user's inquiry and that it has no child responses before allowing the edit.
     *
     * @param userId The ID of the user.
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the response.
     * @param patchResponseRequestDto The DTO containing the updated response content.
     * @throws NotFoundException if the inquiry or response is not found.
     * @throws InvalidOperationException if the response does not belong to the inquiry.
     * @throws HasRelatedEntitiesException if the response has child responses and cannot be edited.
     */
    @Override
    @Transactional
    public void editInquiryResponseForUser(Long userId, Long inquiryId, Long responseId, PatchResponseRequestDto patchResponseRequestDto) {
        findInquiryOrThrow(inquiryId, userId);
        InquiryResponse response = findInquiryResponseOrThrow(responseId);

        validateInquiryResponseBelongsToInquiry(response, inquiryId);
        validateResponseHasNoChildResponses(response);

        response.updateContent(patchResponseRequestDto.getContent());
        inquiryResponseRepository.save(response);
    }

    /**
     * Deletes a response within a user's inquiry.
     * This method ensures that the response belongs to the user's inquiry and that it has no child responses before allowing deletion.
     *
     * @param userId The ID of the user.
     * @param inquiryId The ID of the inquiry.
     * @param responseId The ID of the response.
     * @throws NotFoundException if the inquiry or response is not found.
     * @throws InvalidOperationException if the response does not belong to the inquiry.
     * @throws HasRelatedEntitiesException if the response has child responses and cannot be deleted.
     */
    @Override
    @Transactional
    public void deleteInquiryResponseForUser(Long userId, Long inquiryId, Long responseId) {
        findInquiryOrThrow(inquiryId, userId);
        InquiryResponse response = findInquiryResponseOrThrow(responseId);

        validateInquiryResponseBelongsToInquiry(response, inquiryId);
        validateResponseHasNoChildResponses(response);

        inquiryResponseRepository.delete(response);
    }



    private void validateInquiryResponseBelongsToInquiry(InquiryResponse response, Long inquiryId) {
        if (!response.getInquiry().getId().equals(inquiryId)) {
            throw new InvalidOperationException(MESSAGE_400_InvalidResponseId);
        }
    }


    private static void validateResponseHasNoChildResponses(InquiryResponse response) {
        if (!response.getChildResponses().isEmpty()) {
            throw new HasRelatedEntitiesException(MESSAGE_409_RelationConflict);
        }
    }


    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
    }

    private Inquiry findInquiryOrThrow(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_InquiryNotFound));
    }

    private Inquiry findInquiryOrThrow(Long inquiryId, Long userId) {
        return inquiryRepository.findByIdAndUsersId(inquiryId, userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_InquiryNotFound));
    }

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ItemNotFound));
    }

    private InquiryResponse findInquiryResponseOrThrow(Long responseId) {
        return inquiryResponseRepository.findById(responseId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ResponseNotFound));
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

    private InquiryDetailsResponseDto.ResponseDto mapInquiryResponseToDto(InquiryResponse response) {
        List<InquiryDetailsResponseDto.ResponseDto> childResponses = mapInquiryResponsesToDtoList(response);
        return getResponseDto(response, childResponses);
    }

    private List<InquiryDetailsResponseDto.ResponseDto> mapInquiryResponsesToDtoList(InquiryResponse response) {
        return getResponseDtoList(response);
    }

    private List<InquiryDetailsResponseDto.ResponseDto> getResponseDtoList(InquiryResponse response) {
        return response.getChildResponses() != null
                ? response.getChildResponses().stream()
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
