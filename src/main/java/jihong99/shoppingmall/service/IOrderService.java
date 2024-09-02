package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.response.order.OrderDetailsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {

    Page<OrderDetailsResponseDto> getUserOrderDetails(Long userId, Pageable pageable);
}
