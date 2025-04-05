package com.haven.dto;

import java.util.List;

public class SearchResponseDTO {
	private ShopSearchDTO shop; // Chỉ một shop
    private List<ProductSearchDTO> products;
	public ShopSearchDTO getShop() {
		return shop;
	}
	public void setShop(ShopSearchDTO shop) {
		this.shop = shop;
	}
	public List<ProductSearchDTO> getProducts() {
		return products;
	}
	public void setProducts(List<ProductSearchDTO> products) {
		this.products = products;
	}
    
    
}
