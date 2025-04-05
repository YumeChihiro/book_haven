package com.haven.dto;

import java.math.BigDecimal;

public class ProductSearchDTO {
	private int productId;
    private String name;
    private BigDecimal price;
    private long totalSold;
    private byte[] picture;
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public long getTotalSold() {
		return totalSold;
	}
	public void setTotalSold(long totalSold) {
		this.totalSold = totalSold;
	}
	public byte[] getPicture() {
		return picture;
	}
	public void setPicture(byte[] picture) {
		this.picture = picture;
	}
    
    
}
