package jihong99.shoppingmall.controller;

import jihong99.shoppingmall.dto.PaginatedResponseDto;
import jihong99.shoppingmall.dto.UserSummaryDto;
import jihong99.shoppingmall.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final IAdminService adminService;

    /**
     * Retrieves a summary list of users.
     *
     * @param page the page number to retrieve (for pagination)
     * @param size the number of users to retrieve per page (maximum 10)
     * @return a paginated response containing the user summaries
     */
    @GetMapping("/users")
    public PaginatedResponseDto<UserSummaryDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserSummaryDto> userSummaries = adminService.getUsers(pageable);
        return PaginatedResponseDto.of(userSummaries);
    }
}