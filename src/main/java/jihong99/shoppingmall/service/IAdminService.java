package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.UserSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdminService {

    Page<UserSummaryDto> getUsers(Pageable pageable);


}
