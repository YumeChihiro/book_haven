package com.haven.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminCommissionStatsDTO {
	private List<OrderResponseDTO> completedOrders; // Danh sách đơn hàng đã hoàn thành
	private BigDecimal totalCommission; // Tổng hoa hồng

	public List<OrderResponseDTO> getCompletedOrders() {
		return completedOrders;
	}

	public void setCompletedOrders(List<OrderResponseDTO> completedOrders) {
		this.completedOrders = completedOrders;
	}

	public BigDecimal getTotalCommission() {
		return totalCommission;
	}

	public void setTotalCommission(BigDecimal totalCommission) {
		this.totalCommission = totalCommission;
	}
}