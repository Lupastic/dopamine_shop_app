package com.dopamineshop.catalog;

import com.dopamineshop.catalog.dto.CreateProductRequest;
import com.dopamineshop.catalog.dto.ProductFeedResponse;
import com.dopamineshop.catalog.dto.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductFeedResponse> getFeed(
            @RequestParam(required = false) String category,
            @RequestParam(name = "exclusive", required = false, defaultValue = "false") boolean exclusiveOnly,
            @RequestParam(required = false) String cursor
    ) {
        return ResponseEntity.ok(productService.getFeed(category, exclusiveOnly, cursor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // TODO: ограничить ROLE_ADMIN, когда появятся роли (сейчас MVP — любой authenticated)
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }
}
