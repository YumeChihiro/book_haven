package com.haven.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.ManagerOrderStatsDTO;
import com.haven.dto.OrderDetailDTO;
import com.haven.dto.OrderResponseDTO;
import com.haven.dto.ProductSalesDTO;
import com.haven.entity.Order;
import com.haven.entity.OrderDetail;
import com.haven.entity.OrderStatus;
import com.haven.entity.OrderTracking;
import com.haven.entity.OrderVoucher;
import com.haven.entity.PaymentStatus;
import com.haven.entity.Shop_Product;
import com.haven.entity.Transaction;
import com.haven.repository.OrderDetailRepository;
import com.haven.repository.OrderRepository;
import com.haven.repository.OrderStatusRepository;
import com.haven.repository.OrderTrackingRepository;
import com.haven.repository.OrderVoucherRepository;
import com.haven.repository.ShopProductRepository;
import com.haven.repository.TransactionRepository;

@Service
public class OrderShopService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	
	@Autowired
	private OrderVoucherRepository orderVoucherRepository;
	
	@Autowired
	private OrderStatusRepository orderStatusRepository;
	
	@Autowired
	private ShopProductRepository shopProductRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private OrderTrackingRepository orderTrackingRepository;
	
	@Transactional(readOnly = true)
	public List<OrderResponseDTO> getShopOrders(Integer managerId) {
		
	    // Lấy danh sách đơn hàng của shop (manager)
	    List<Order> orders = orderRepository.findByManagerManagerId(managerId);

	    // Chuyển đổi sang DTO
	    return orders.stream().map(order -> {
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
	}
	
	@Transactional(readOnly = true)
	public OrderResponseDTO getShopOrderDetails(Integer orderId, Integer managerId) {
	    // Tìm đơn hàng
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

	    // Lấy danh sách order details
	    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(orderId);

	    // Tạo response
	    OrderResponseDTO response = new OrderResponseDTO();
	    response.setOrderId(order.getOrderId());
	    response.setTotal(order.getTotal());
	    response.setShippingFee(order.getShippingFee());
	    response.setCommissionFee(order.getCommissionFee());
	    response.setDiscountAmount(orderVoucherRepository.findByOrderOrderId(orderId)
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
	}
	
	@Transactional
	public OrderResponseDTO updateOrderStatus(Integer orderId, Integer managerId, Integer newStatusId) {
	    // Tìm đơn hàng
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

	    // Lấy trạng thái mới
	    OrderStatus newStatus = orderStatusRepository.findById(newStatusId)
	            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái với ID: " + newStatusId));

	    // Kiểm tra trạng thái hiện tại và trạng thái mới
	    int currentStatusId = order.getOrderStatus().getOrderstatusId();
	    if (currentStatusId == 4) { // Cancelled
	        throw new IllegalArgumentException("Không thể cập nhật trạng thái cho đơn hàng đã bị hủy");
	    }
	    if (currentStatusId == 3 && newStatusId != 4) { // Delivered -> chỉ có thể chuyển sang Cancelled
	        throw new IllegalArgumentException("Đơn hàng đã giao không thể chuyển sang trạng thái khác ngoài Cancelled");
	    }
	    if (newStatusId == 1) { // Không cho phép chuyển về Pending
	        throw new IllegalArgumentException("Không thể chuyển trạng thái về Pending");
	    }

	    // Cập nhật trạng thái đơn hàng
	    order.setOrderStatus(newStatus);
	    Order updatedOrder = orderRepository.save(order);

	    // Nếu chuyển sang Cancelled, hoàn lại tồn kho
	    if (newStatusId == 4) { // Cancelled
	        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(orderId);
	        for (OrderDetail detail : orderDetails) {
	            Shop_Product shopProduct = shopProductRepository.findByProductProductId(detail.getProduct().getProductId());
	            if (shopProduct != null) {
	                shopProduct.setQuantity(shopProduct.getQuantity() + detail.getQuantity());
	                shopProductRepository.save(shopProduct);
	            }
	        }

	        // Cập nhật giao dịch
	        Transaction transaction = transactionRepository.findByOrderOrderId(orderId)
	                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch cho đơn hàng với ID: " + orderId));
	        transaction.setPaymentStatus(PaymentStatus.REFUNDED);
	        transactionRepository.save(transaction);
	    }

	    // Ghi lại lịch sử trạng thái đơn hàng trong order_tracking
	    OrderTracking tracking = new OrderTracking();
	    tracking.setOrder(updatedOrder);
	    tracking.setOrderStatus(newStatus);
	    tracking.setChangedAt(LocalDateTime.now());
	    orderTrackingRepository.save(tracking);

	    // Tạo response
	    OrderResponseDTO response = new OrderResponseDTO();
	    response.setOrderId(updatedOrder.getOrderId());
	    response.setTotal(updatedOrder.getTotal());
	    response.setShippingFee(updatedOrder.getShippingFee());
	    response.setCommissionFee(updatedOrder.getCommissionFee());
	    response.setDiscountAmount(orderVoucherRepository.findByOrderOrderId(orderId)
	            .map(OrderVoucher::getDiscountAmount)
	            .orElse(BigDecimal.ZERO));
	    response.setStatus(updatedOrder.getOrderStatus().getName());

	    // Thêm thông tin chi tiết đơn hàng (danh sách sản phẩm)
	    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(orderId);
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
	}
	
	@Transactional(readOnly = true)
	public ManagerOrderStatsDTO getManagerOrderStats(Integer managerId) {

	    // Lấy danh sách đơn hàng của manager
	    List<Order> orders = orderRepository.findByManagerManagerId(managerId);

	    // 1. Thống kê số lượng đơn hàng theo trạng thái
	    Map<String, Long> ordersByStatus = orders.stream()
	            .collect(Collectors.groupingBy(
	                    order -> order.getOrderStatus().getName(),
	                    Collectors.counting()
	            ));

	    // 2. Tính tổng số tiền kiếm được và thống kê sản phẩm đã bán (chỉ từ đơn hàng Delivered)
	    BigDecimal totalRevenue = BigDecimal.ZERO;
	    Long totalProductsSold = 0L;
	    Map<Integer, ProductSalesDTO> productSalesMap = new HashMap<>();

	    for (Order order : orders) {
	        if (order.getOrderStatus().getOrderstatusId() == 3) { // 3 = Delivered
	            // Cộng dồn tổng số tiền kiếm được
	            totalRevenue = totalRevenue.add(order.getTotal());

	            // Lấy danh sách order details
	            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());
	            for (OrderDetail detail : orderDetails) {
	                // Cộng dồn tổng số sản phẩm đã bán
	                totalProductsSold += detail.getQuantity();

	                // Thống kê chi tiết sản phẩm
	                Integer productId = detail.getProduct().getProductId();
	                ProductSalesDTO productSales = productSalesMap.computeIfAbsent(productId, k -> {
	                    ProductSalesDTO dto = new ProductSalesDTO();
	                    dto.setProductId(productId);
	                    dto.setProductName(detail.getProduct().getName());
	                    dto.setQuantitySold(0L);
	                    return dto;
	                });
	                productSales.setQuantitySold(productSales.getQuantitySold() + detail.getQuantity());
	            }
	        }
	    }

	    // Chuyển productSalesMap thành danh sách
	    List<ProductSalesDTO> productSalesDetails = new ArrayList<>(productSalesMap.values());

	    // Tạo response thống kê
	    ManagerOrderStatsDTO stats = new ManagerOrderStatsDTO();
	    stats.setOrdersByStatus(ordersByStatus);
	    stats.setTotalRevenue(totalRevenue);
	    stats.setTotalProductsSold(totalProductsSold);
	    stats.setProductSalesDetails(productSalesDetails);

	    return stats;
	}
}
