package com.haven.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.AdminCommissionStatsDTO;
import com.haven.dto.OrderDetailDTO;
import com.haven.dto.OrderResponseDTO;
import com.haven.entity.Order;
import com.haven.entity.OrderDetail;
import com.haven.entity.OrderVoucher;
import com.haven.repository.OrderDetailRepository;
import com.haven.repository.OrderRepository;
import com.haven.repository.OrderVoucherRepository;

@Service
public class OrderAdminService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	
	@Autowired
	private OrderVoucherRepository orderVoucherRepository;

	@Transactional(readOnly = true)
	public AdminCommissionStatsDTO getAdminCommissionStats(Integer adminId) {

	    // Lấy danh sách đơn hàng đã hoàn thành (Delivered, order_status_id = 3)
	    List<Order> completedOrders = orderRepository.findByOrderStatusOrderstatusId(3); // 3 = Delivered

	    // Tính tổng hoa hồng và chuyển đổi đơn hàng sang DTO
	    BigDecimal totalCommission = BigDecimal.ZERO;
	    List<OrderResponseDTO> orderDTOs = completedOrders.stream().map(order -> {
	        // Lấy danh sách order details
	        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());

	        // Tạo response cho từng đơn hàng
	        OrderResponseDTO response = new OrderResponseDTO();
	        response.setOrderId(order.getOrderId());
	        response.setTotal(order.getTotal());
	        response.setShippingFee(order.getShippingFee());
	        response.setCommissionFee(order.getCommissionFee());
	        response.setDiscountAmount(orderVoucherRepository.findByOrderOrderId(order.getOrderId())
	                .map(OrderVoucher::getDiscountAmount)
	                .orElse(BigDecimal.ZERO));
	        response.setStatus(order.getOrderStatus().getName());

	        // Thêm thông tin chi tiết đơn hàng (danh sách sản phẩm)
	        List<OrderDetailDTO> detailDTOs = orderDetails.stream().map(detail -> {
	            OrderDetailDTO detailDTO = new OrderDetailDTO();
	            detailDTO.setProductId(detail.getProduct().getProductId());
	            detailDTO.setProductName(detail.getProduct().getName());
	            detailDTO.setQuantity(detail.getQuantity());
	            detailDTO.setPrice(detail.getPrice());
	            detailDTO.setSubtotal(detail.getSubtotal());
	            return detailDTO;
	        }).collect(Collectors.toList());

	        response.setOrderDetails(detailDTOs);

	        return response;
	    }).collect(Collectors.toList());

	    // Tính tổng hoa hồng từ các đơn hàng đã hoàn thành
	    for (Order order : completedOrders) {
	        totalCommission = totalCommission.add(order.getCommissionFee());
	    }

	    // Tạo response thống kê
	    AdminCommissionStatsDTO stats = new AdminCommissionStatsDTO();
	    stats.setCompletedOrders(orderDTOs);
	    stats.setTotalCommission(totalCommission);

	    return stats;
	}
}
