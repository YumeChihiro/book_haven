package com.haven.dto;

import java.util.List;

public class CartResponseDTO {
    private Integer cartId;
    private Integer customerId;
    private List<CartItemDTO> items;

    public CartResponseDTO() {
    }

    public CartResponseDTO(Integer cartId, Integer customerId, List<CartItemDTO> items) {
        this.cartId = cartId;
        this.customerId = customerId;
        this.items = items;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}