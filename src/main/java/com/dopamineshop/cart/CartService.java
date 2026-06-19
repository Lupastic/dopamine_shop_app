package com.dopamineshop.cart;

import com.dopamineshop.cart.dto.AddToCartRequest;
import com.dopamineshop.cart.dto.CartItemResponse;
import com.dopamineshop.cart.dto.CartResponse;
import com.dopamineshop.catalog.Product;
import com.dopamineshop.catalog.ProductRepository;
import com.dopamineshop.common.exception.ApiException;
import com.dopamineshop.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponse getCart(User user) {
        return toCartResponse(cartItemRepository.findAllByUserId(user.getId()));
    }

    @Transactional
    public CartResponse addItem(User user, AddToCartRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Товар не найден"));

        int quantityToAdd = request.quantity() != null ? request.quantity() : 1;

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantityToAdd);
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(quantityToAdd)
                        .build());

        cartItemRepository.save(item);
        return getCart(user);
    }

    @Transactional
    public CartResponse updateQuantity(User user, UUID productId, int quantity) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Товар отсутствует в корзине"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return getCart(user);
    }

    @Transactional
    public CartResponse removeItem(User user, UUID productId) {
        cartItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
        return getCart(user);
    }

    /** Используется при оформлении заказа (Sprint 3) для очистки корзины после checkout. */
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUserId(user.getId());
    }

    private CartResponse toCartResponse(List<CartItem> items) {
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalCount = itemResponses.stream().mapToInt(CartItemResponse::quantity).sum();

        String currency = itemResponses.isEmpty() ? "KZT" : itemResponses.get(0).currency();

        return new CartResponse(itemResponses, total, currency, totalCount);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Product p = item.getProduct();
        BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                p.getId(), p.getTitle(), p.getImageUrl(), p.getPrice(), p.getCurrency(),
                item.getQuantity(), lineTotal
        );
    }
}
