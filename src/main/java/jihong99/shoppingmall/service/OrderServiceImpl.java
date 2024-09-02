package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.response.order.OrderDetailsResponseDto;
import jihong99.shoppingmall.dto.response.order.OrderDetailsResponseDto.OrderItemResponseDto;
import jihong99.shoppingmall.entity.Coupon;
import jihong99.shoppingmall.entity.OrderDetails;
import jihong99.shoppingmall.entity.Orders;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.OrderDetailsRepository;
import jihong99.shoppingmall.repository.OrderItemRepository;
import jihong99.shoppingmall.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;


    /**
     * Retrieves a paginated list of order details for a specific user.
     *
     * <p>This method fetches a page of order details for the specified user and maps
     * each order detail entity to a response DTO.</p>
     *
     * @param userId   the ID of the user whose orders are to be retrieved
     * @param pageable the pagination information
     * @return a page of order details response DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDetailsResponseDto> getUserOrderDetails(Long userId, Pageable pageable) {
        Orders order = findOrderOrThrow(userId);
        return orderDetailsRepository.findAllByOrdersId(order.getId(), pageable)
                .map(orderDetail -> {
                    List<OrderItemResponseDto> items = mapOrderItemsToResponseDtos(orderDetail);
                    return createOrderDetailsResponseDto(orderDetail, items);
                });
    }


    private static OrderDetailsResponseDto createOrderDetailsResponseDto(OrderDetails orderDetail, List<OrderItemResponseDto> items) {
        return new OrderDetailsResponseDto(
                orderDetail.getId(),
                orderDetail.getOrders().getOrderNumber(),
                orderDetail.getOrders().getOrderDate(),
                orderDetail.getOrders().getOrderStatus(),
                items,
                orderDetail.getOrders().getFinalAmount(),
                orderDetail.getDeliveryAddress().getName(),
                orderDetail.getDeliveryAddress().getPhoneNumber(),
                orderDetail.getDeliveryAddress().getZipCode(),
                orderDetail.getDeliveryAddress().getAddress(),
                orderDetail.getDeliveryAddress().getAddressDetail(),
                Optional.ofNullable(orderDetail.getAppliedCoupon())
                        .map(Coupon::getCode)
                        .orElse(null),
                orderDetail.getTotal_amount(),
                orderDetail.getDiscount_amount()
        );
    }

    private List<OrderItemResponseDto> mapOrderItemsToResponseDtos(OrderDetails orderDetail) {
        return orderItemRepository.findAllByOrdersId(orderDetail.getOrders().getId()).stream()
                .map(orderItem -> new OrderItemResponseDto(
                        orderItem.getItem().getName(),
                        orderItem.getItem().getPrice(),
                        orderItem.getQuantity(),
                        orderItem.getTotalPrice()
                ))
                .collect(Collectors.toList());
    }


    private Orders findOrderOrThrow(Long userId) {
        return orderRepository.findByUsersId(userId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_UserNotFound)
        );
    }
}
