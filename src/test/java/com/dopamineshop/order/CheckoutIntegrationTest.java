package com.dopamineshop.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class CheckoutIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("app.jwt.secret", () -> "test-secret-at-least-thirty-two-characters-long");
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void customerCanCheckoutCartAndReadOrder() throws Exception {
        String email = "checkout-" + java.util.UUID.randomUUID() + "@example.com";
        JsonNode auth = postJson("/auth/register", null, """
                {"email":"%s","password":"password123","displayName":"Checkout test"}
                """.formatted(email));
        String token = auth.get("accessToken").asText();
        String refreshToken = auth.get("refreshToken").asText();

        mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/cart").header("Authorization", bearer(refreshToken)))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/cart").header("Authorization", bearer("invalid.jwt.token")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + token + "\"}"))
                .andExpect(status().isUnauthorized());

        JsonNode product = postJson("/products", token, """
                {"title":"Test bag","price":3500.00,"category":"bags","imageUrl":"https://example.com/bag.jpg","exclusive":false}
                """);
        String productId = product.get("id").asText();

        postJson("/cart/items", token, """
                {"productId":"%s","quantity":2}
                """.formatted(productId));
        JsonNode address = postJson("/users/me/addresses", token, """
                {"label":"Home","city":"Almaty","street":"Abay","house":"10","isDefault":true}
                """);

        JsonNode order = postJson("/orders/checkout", token, """
                {"addressId":"%s"}
                """.formatted(address.get("id").asText()));
        assertThat(order.get("totalAmount").decimalValue()).isEqualByComparingTo("7000.00");
        assertThat(order.get("items").size()).isEqualTo(1);
        assertThat(order.get("status").asText()).isEqualTo("PROCESSING");

        mvc.perform(get("/cart").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItemsCount").value(0));
        mvc.perform(get("/orders/{id}", order.get("id").asText())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    private JsonNode postJson(String path, String token, String json) throws Exception {
        var request = post(path).contentType(MediaType.APPLICATION_JSON).content(json);
        if (token != null) {
            request.header("Authorization", bearer(token));
        }
        String body = mvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body);
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
