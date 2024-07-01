package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.UserSummaryDto;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

    private final UserRepository userRepository;

    @Override
    public Page<UserSummaryDto> getUsers(Pageable pageable) {
        return userRepository.findAllUserSummaries(pageable);
    }
}