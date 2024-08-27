package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.Inquiry;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findAllByTypeAndStatus(InquiryType type, InquiryStatus status, Pageable pageable);
}
