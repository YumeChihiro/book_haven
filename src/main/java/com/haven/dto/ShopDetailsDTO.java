package com.haven.dto;

import java.util.List;

public class ShopDetailsDTO {
    private Integer managerId;
    private String managerName;
    private long availableProducts; // Số lượng sản phẩm hiện có
    private long totalSoldProducts; // Số lượng sản phẩm đã bán
    private Double averageRating;   // Điểm đánh giá trung bình
    private List<ProductSearchDTO> products; // Danh sách sản phẩm của shop

    public ShopDetailsDTO() {
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public long getAvailableProducts() {
        return availableProducts;
    }

    public void setAvailableProducts(long availableProducts) {
        this.availableProducts = availableProducts;
    }

    public long getTotalSoldProducts() {
        return totalSoldProducts;
    }

    public void setTotalSoldProducts(long totalSoldProducts) {
        this.totalSoldProducts = totalSoldProducts;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<ProductSearchDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSearchDTO> products) {
        this.products = products;
    }
}