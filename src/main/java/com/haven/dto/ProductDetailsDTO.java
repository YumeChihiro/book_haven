package com.haven.dto;

import java.math.BigDecimal;

public class ProductDetailsDTO {

    private Integer productId;
    private String name;
    private Double price;
    private byte[] picture;
    private Long totalSold;

    public ProductDetailsDTO(Integer productId, String name, BigDecimal price, byte[] picture, Long totalSold) {
        this.productId = productId;
        this.name = name;
        this.price = (price != null) ? price.doubleValue() : null;
        this.picture = picture;
        this.totalSold = totalSold;
    }

	// Getters và Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Long totalSold) {
        this.totalSold = totalSold;
    }
}