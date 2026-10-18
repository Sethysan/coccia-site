
package com.cocciahouse.api.controller;

import com.cocciahouse.api.dto.purchasing.PurchasingProductRequest;
import com.cocciahouse.api.dto.purchasing.PurchasingProductResponse;
import com.cocciahouse.api.service.PurchasingProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing/products")
public class AdminPurchasingProductController {

    private final PurchasingProductService productService;

    public AdminPurchasingProductController(
            PurchasingProductService productService
    ) {
        this.productService = productService;
    }

    @GetMapping
    public List<PurchasingProductResponse> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public PurchasingProductResponse getProduct(
            @PathVariable Long id
    ) {
        return productService.getProduct(id);
    }

    @PostMapping
    public ResponseEntity<PurchasingProductResponse> createProduct(
            @Valid @RequestBody PurchasingProductRequest request
    ) {
        PurchasingProductResponse created =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public PurchasingProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody PurchasingProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }
}
