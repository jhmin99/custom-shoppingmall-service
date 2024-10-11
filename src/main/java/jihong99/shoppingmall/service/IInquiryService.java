package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.inquiry.ChildResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.InquiryRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.PatchResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.ResponseRequestDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryDetailsResponseDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryResponseDto;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IInquiryService {
    void respondToInquiry(Long inquiryId, ResponseRequestDto responseRequestDto);

    void editInquiryResponse(Long inquiryId, Long respondId, PatchResponseRequestDto patchRepondRequestDto);

    void deleteInquiryResponse(Long inquiryId, Long respondId);

    Page<InquiryResponseDto> getAllInquiries(InquiryType type, InquiryStatus status, Pageable pageable);

    InquiryDetailsResponseDto getInquiryDetails(Long inquiryId);

    void respondToParentResponse(Long inquiryId, Long responseId, ChildResponseRequestDto responseRequestDto);

    void submitItemInquiry(Long userId, Long itemId, InquiryRequestDto inquiryRequestDto);

    void submitCustomerInquiry(Long userId, InquiryRequestDto inquiryRequestDto);

    Page<InquiryResponseDto> getAllUserInquiries(Long userId, Pageable pageable);

    InquiryDetailsResponseDto getUserInquiryDetails(Long userId, Long inquiryId);

    void deleteInquiry(Long userId, Long inquiryId);

    void respondToParentResponseForUser(Long userId, Long inquiryId, Long responseId, ChildResponseRequestDto responseRequestDto);

    void editInquiryResponseForUser(Long userId, Long inquiryId, Long responseId, PatchResponseRequestDto patchRespondRequestDto);

    void deleteInquiryResponseForUser(Long userId, Long inquiryId, Long responseId);
}
