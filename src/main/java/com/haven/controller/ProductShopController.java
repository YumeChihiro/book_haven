package com.haven.controller;

import com.haven.dto.ProductRequestDTO;
import com.haven.dto.ProductResponseDTO;
import com.haven.service.ShopProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop/product-management")
@Tag(name = "Product shop api", description = "Crud sản phẩm của shop")
@SecurityRequirement(name = "JWT")
public class ProductShopController {

    @Autowired
    private ShopProductService productService;

    @GetMapping
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Lấy tất cả sản phẩm của shop"
		)
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Xem chi tiết sản phẩm nhất định"
		)
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable("productId") int productId) {
        ProductResponseDTO product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Thêm sản phẩm"
		)
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductRequestDTO dto) {
        ProductResponseDTO newProduct = productService.addProduct(dto);
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Cập nhật sản phẩm"
		)
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable("productId") int productId,
            @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Xóa sản phẩm"
		)
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") int productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }
}