package com.haven.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ManagerOrderStatsDTO {
	private Map<String, Long> ordersByStatus; // Số lượng đơn hàng theo trạng thái
	private BigDecimal totalRevenue; // Tổng số tiền kiếm được (từ đơn hàng Delivered)
	private Long totalProductsSold; // Tổng số sản phẩm đã bán (từ đơn hàng Delivered)
	private List<ProductSalesDTO> productSalesDetails; // Chi tiết sản phẩm đã bán

	// Getters and Setters
	public Map<String, Long> getOrdersByStatus() {
		return ordersByStatus;
	}

	public void setOrdersByStatus(Map<String, Long> ordersByStatus) {
		this.ordersByStatus = ordersByStatus;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public Long getTotalProductsSold() {
		return totalProductsSold;
	}

	public void setTotalProductsSold(Long totalProductsSold) {
		this.totalProductsSold = totalProductsSold;
	}

	public List<ProductSalesDTO> getProductSalesDetails() {
		return productSalesDetails;
	}

	public void setProductSalesDetails(List<ProductSalesDTO> productSalesDetails) {
		this.productSalesDetails = productSalesDetails;
	}
}