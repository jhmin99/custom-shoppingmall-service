package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.InquiryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryResponseRepository extends JpaRepository<InquiryResponse, Long> {
}
