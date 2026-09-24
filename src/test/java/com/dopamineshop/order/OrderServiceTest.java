package com.dopamineshop.order;

import com.dopamineshop.cart.CartItem;
import com.dopamineshop.cart.CartItemRepository;
import com.dopamineshop.catalog.Product;
import com.dopamineshop.common.exception.ApiException;
import com.dopamineshop.notification.NotificationService;
import com.dopamineshop.order.dto.CreateOrderRequest;
import com.dopamineshop.user.User;
import com.dopamineshop.user.UserAddress;
import com.dopamineshop.user.UserAddressRepository;
import com.dopamineshop.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock CartItemRepository cartItemRepository;
    @Mock UserAddressRepository addressRepository;
    @Mock UserRepository userRepository;
    @Mock NotificationService notificationService;

    @InjectMocks OrderService orderService;

    @Test
    void checkoutCreatesOrderAndClearsCart() {
        User user = User.builder().id(UUID.randomUUID()).email("buyer@example.com")
                .passwordHash("hash").build();
        Product product = Product.builder().id(UUID.randomUUID()).title("Bag")
                .imageUrl("https://example.com/bag.jpg")
                .price(new BigDecimal("3500.00")).currency("KZT").build();
        UserAddress address = UserAddress.builder().id(UUID.randomUUID()).user(user)
                .city("Almaty").street("Abay").isDefault(true).build();
        CartItem item = CartItem.builder().user(user).product(product).quantity(2).build();

        when(cartItemRepository.findAllByUserId(user.getId())).thenReturn(List.of(item));
        when(addressRepository.findByIdAndUserId(address.getId(), user.getId()))
                .thenReturn(Optional.of(address));
        when(orderRepository.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));

        var response = orderService.checkout(user, new CreateOrderRequest(address.getId()));

        assertThat(response.totalAmount()).isEqualByComparingTo("7000.00");
        assertThat(response.status()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
        assertThat(response.statusHistory()).hasSize(1);
        assertThat(user.getTotalOrdersCount()).isEqualTo(1);
        verify(cartItemRepository).deleteByUserId(user.getId());
    }

    @Test
    void checkoutRejectsEmptyCartWithoutCreatingOrder() {
        User user = User.builder().id(UUID.randomUUID()).email("buyer@example.com")
                .passwordHash("hash").build();
        when(cartItemRepository.findAllByUserId(user.getId())).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.checkout(user, new CreateOrderRequest(UUID.randomUUID())))
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
