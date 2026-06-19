package com.dopamineshop.order;

import com.dopamineshop.cart.CartItem;
import com.dopamineshop.cart.CartItemRepository;
import com.dopamineshop.common.exception.ApiException;
import com.dopamineshop.notification.NotificationService;
import com.dopamineshop.order.dto.CreateOrderRequest;
import com.dopamineshop.order.dto.OrderItemResponse;
import com.dopamineshop.order.dto.OrderResponse;
import com.dopamineshop.order.dto.OrderStatusHistoryResponse;
import com.dopamineshop.user.User;
import com.dopamineshop.user.UserAddress;
import com.dopamineshop.user.UserAddressRepository;
import com.dopamineshop.user.UserRepository;
import com.dopamineshop.user.dto.UserAddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public OrderResponse checkout(User user, CreateOrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        UserAddress address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Address not found"));

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Instant now = Instant.now();
        Order order = Order.builder()
                .user(user)
                .address(address)
                .totalAmount(totalAmount)
                .currency(cartItems.get(0).getProduct().getCurrency())
                .status(OrderStatus.PROCESSING)
                .placedAt(now)
                .estimatedArrival(now.plus(Duration.ofDays(ThreadLocalRandom.current().nextLong(3, 8))))
                .nextStatusAt(nextStatusAt(OrderStatus.PROCESSING, now))
                .build();

        for (CartItem cartItem : cartItems) {
            order.addItem(OrderItem.builder()
                    .product(cartItem.getProduct())
                    .title(cartItem.getProduct().getTitle())
                    .imageUrl(cartItem.getProduct().getImageUrl())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getProduct().getPrice())
                    .build());
        }
        order.recordStatusChange(OrderStatus.PROCESSING, now);

        Order saved = orderRepository.save(order);
        user.setTotalSavedAmount(user.getTotalSavedAmount().add(totalAmount));
        user.setTotalOrdersCount(user.getTotalOrdersCount() + 1);
        userRepository.save(user);
        cartItemRepository.deleteByUserId(user.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(User user) {
        return orderRepository.findAllByUserId(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(User user, UUID orderId) {
        Order order = orderRepository.findDetailsByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<String> favoriteCategories(User user, int limit) {
        return orderRepository.findFavoriteCategories(user.getId(), PageRequest.of(0, limit)).stream()
                .map(row -> (String) row[0])
                .toList();
    }

    @Transactional
    public void advanceDueStatuses() {
        Instant now = Instant.now();
        for (Order order : orderRepository.findOrdersDueForStatusChange(now)) {
            order.getStatus().next().ifPresent(next -> {
                order.setStatus(next);
                order.recordStatusChange(next, now);
                if (next == OrderStatus.ARRIVED) {
                    order.setArrivedAt(now);
                    order.setNextStatusAt(null);
                } else {
                    order.setNextStatusAt(nextStatusAt(next, now));
                }
                orderRepository.save(order);
                notificationService.sendStatusUpdate(order.getUser(), order, next);
            });
        }
    }

    private Instant nextStatusAt(OrderStatus status, Instant from) {
        return status.timeToNext()
                .map(duration -> from.plus(jitter(duration)))
                .orElse(null);
    }

    private Duration jitter(Duration base) {
        long seconds = base.toSeconds();
        long min = Math.max(60, Math.round(seconds * 0.8));
        long max = Math.max(min + 1, Math.round(seconds * 1.2));
        return Duration.ofSeconds(ThreadLocalRandom.current().nextLong(min, max));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getId() == null ? new UUID(0, 0) : item.getId()))
                .map(item -> new OrderItemResponse(
                        item.getProduct() != null ? item.getProduct().getId() : null,
                        item.getTitle(),
                        item.getImageUrl(),
                        item.getQuantity(),
                        item.getPriceAtPurchase(),
                        item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        List<OrderStatusHistoryResponse> history = order.getStatusHistory().stream()
                .map(item -> new OrderStatusHistoryResponse(item.getStatus(), item.getChangedAt()))
                .toList();

        UserAddress address = order.getAddress();
        UserAddressDto addressDto = address != null ? new UserAddressDto(
                address.getId(),
                address.getLabel(),
                address.getCity(),
                address.getStreet(),
                address.getHouse(),
                address.getApartment(),
                address.getEntrance(),
                address.getFloor(),
                address.getComment(),
                address.getIsDefault()
        ) : null;

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getStatus(),
                order.getPlacedAt(),
                order.getEstimatedArrival(),
                order.getArrivedAt(),
                addressDto,
                items,
                history
        );
    }
}