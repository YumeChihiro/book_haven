package com.haven.dto;

import java.math.BigDecimal;

public class CartItemDTO {
    private Integer cartItemId;
    private Integer productId;
    private String productName; // Thêm để hiển thị thông tin sản phẩm
    private Integer quantity;
    private BigDecimal addedPrice;
    private Boolean selected;

    public CartItemDTO() {
    }

    public CartItemDTO(Integer cartItemId, Integer productId, String productName, Integer quantity, BigDecimal addedPrice, Boolean selected) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.addedPrice = addedPrice;
        this.selected = selected;
    }

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAddedPrice() {
        return addedPrice;
    }

    public void setAddedPrice(BigDecimal addedPrice) {
        this.addedPrice = addedPrice;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}