package com.haven.service;

import com.haven.dto.OrderDetailDTO;
import com.haven.dto.OrderResponseDTO;
import com.haven.entity.*;
import com.haven.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderCustomerService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;

    @Autowired
    private OrderVoucherRepository orderVoucherRepository;

    @Autowired
    private OrderTrackingRepository orderTrackingRepository;
    
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderDetails(Integer orderId, Integer customerId) {
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

    // Chức năng hủy đơn hàng
    @Transactional
    public OrderResponseDTO cancelOrder(Integer orderId, Integer customerId) {
        // Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra trạng thái đơn hàng (chỉ hủy được nếu đang ở trạng thái Pending)
        if (order.getOrderStatus().getOrderstatusId() != 1) { // 1 = Pending
            throw new IllegalArgumentException("Chỉ có thể hủy đơn hàng khi đang ở trạng thái Pending");
        }

        // Lấy trạng thái Cancelled
        OrderStatus cancelledStatus = orderStatusRepository.findByName("Cancelled")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái đơn hàng 'Cancelled'"));

        // Cập nhật trạng thái đơn hàng
        order.setOrderStatus(cancelledStatus);
        Order updatedOrder = orderRepository.save(order);

        // Hoàn lại tồn kho
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(orderId);
        for (OrderDetail detail : orderDetails) {
            Shop_Product shopProduct = shopProductRepository.findByProductProductId(detail.getProduct().getProductId());
            if (shopProduct != null) {
                shopProduct.setQuantity(shopProduct.getQuantity() + detail.getQuantity());
                shopProductRepository.save(shopProduct);
            }
        }

        Transaction transaction = transactionRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch cho đơn hàng với ID: " + orderId));
        transaction.setPaymentStatus(PaymentStatus.REFUNDED);
        transactionRepository.save(transaction);

        // Ghi lại lịch sử trạng thái đơn hàng trong order_tracking
        OrderTracking tracking = new OrderTracking();
        tracking.setOrder(updatedOrder);
        tracking.setOrderStatus(cancelledStatus);
        tracking.setChangedAt(LocalDateTime.now());
        orderTrackingRepository.save(tracking);

        // Trả về response
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(updatedOrder.getOrderId());
        response.setTotal(updatedOrder.getTotal());
        response.setShippingFee(updatedOrder.getShippingFee());
        response.setCommissionFee(updatedOrder.getCommissionFee());
        response.setDiscountAmount(orderVoucherRepository.findByOrderOrderId(orderId)
                .map(OrderVoucher::getDiscountAmount)
                .orElse(BigDecimal.ZERO)); // Trả về BigDecimal.ZERO nếu không có voucher
        response.setStatus(updatedOrder.getOrderStatus().getName());

        return response;
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderHistory(Integer customerId) {
       
        // Lấy danh sách đơn hàng đã hoàn thành (Delivered) hoặc đã hủy (Cancelled)
        List<Order> orders = orderRepository.findByCustomerCustomerIdAndOrderStatusOrderstatusIdIn(
                customerId, List.of(3, 4)); // 3 = Delivered, 4 = Cancelled

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
}