package jihong99.shoppingmall.dto.response.order;

import jihong99.shoppingmall.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderDetailsResponseDto {
    private Long id;
    private String orderNumber;
    private LocalDate orderDate;
    private OrderStatus orderStatus;
    private List<OrderItemResponseDto> item;
    private Long finalAmount;
    private String receiver;
    private String phoneNumber;
    private Integer zipCode;
    private String address;
    private String addressDetail;
    private String couponCode;
    private Long total_amount;
    private Long discount_amount;

    @Getter
    @AllArgsConstructor
    public static class OrderItemResponseDto {
        private String name;
        private Integer price;
        private Integer quantity;
        private Long totalPrice;
    }

    public static OrderItemResponseDto of(String name, Integer price, Integer quantity, Long totalPrice){
        return new OrderItemResponseDto(name, price, quantity, totalPrice);
    }
}
