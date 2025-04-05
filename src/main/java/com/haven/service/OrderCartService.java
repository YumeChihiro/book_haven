package com.haven.service;

import com.haven.dto.OrderRequestDTO;
import com.haven.dto.OrderResponseDTO;
import com.haven.entity.*;
import com.haven.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderCartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;

    @Autowired
    private ShippingFeeRepository shippingFeeRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private OrderVoucherRepository orderVoucherRepository;

    @Transactional
    public List<OrderResponseDTO> checkoutFromCart(OrderRequestDTO orderRequest) {
        Integer customerId = orderRequest.getCustomerId();
        String shipAddress = orderRequest.getShipAddress();
        Integer paymentId = orderRequest.getPaymentId();
        Integer shipperId = orderRequest.getShipperId();
        Integer voucherId = orderRequest.getVoucherId();
        boolean buyAll = orderRequest.isBuyAll();

        // Lấy giỏ hàng của customer
        Cart cart = cartRepository.findByCustomerCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng trống hoặc không tồn tại"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng không có sản phẩm");
        }

        // Lấy danh sách sản phẩm để mua
        List<CartItem> itemsToBuy;
        if (buyAll) {
            itemsToBuy = cart.getCartItems(); // Mua tất cả sản phẩm trong giỏ
        } else {
            itemsToBuy = cart.getCartItems().stream()
                    .filter(CartItem::getSelected) // Chỉ mua sản phẩm được chọn
                    .collect(Collectors.toList());
            if (itemsToBuy.isEmpty()) {
                throw new IllegalArgumentException("Không có sản phẩm nào được chọn để thanh toán");
            }
        }

        // Kiểm tra tồn kho và lấy giá từ shop_product
        for (CartItem item : itemsToBuy) {
            Shop_Product shopProduct = shopProductRepository.findByProductProductId(item.getProduct().getProductId());
            if (shopProduct == null || shopProduct.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Không đủ hàng cho sản phẩm: " + item.getProduct().getName());
            }
            // Cập nhật giá từ shop_product vào CartItem
            item.setAddedPrice(shopProduct.getPrice());
        }

        // Lấy thông tin cần thiết
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phương thức thanh toán với ID: " + paymentId));
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shipper với ID: " + shipperId));
        OrderStatus initialStatus = orderStatusRepository.findByName("Pending")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái đơn hàng 'Pending'"));

        // Nhóm sản phẩm theo manager_id
        Map<Integer, List<CartItem>> itemsByManager = itemsToBuy.stream()
                .collect(Collectors.groupingBy(item -> 
                    shopProductRepository.findByProductProductId(item.getProduct().getProductId()).getManager().getManagerId()));

        List<OrderResponseDTO> responses = new ArrayList<>();

        // Tạo đơn hàng riêng cho từng manager
        for (Map.Entry<Integer, List<CartItem>> entry : itemsByManager.entrySet()) {
            Integer managerId = entry.getKey();
            List<CartItem> managerItems = entry.getValue();

            // Tính toán tổng tiền, phí vận chuyển, và giảm giá từ voucher (nếu có)
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal shippingFee = BigDecimal.ZERO;
            BigDecimal commissionFee;
            BigDecimal discountAmount = BigDecimal.ZERO;

            for (CartItem item : managerItems) {
            	Shop_Product shopProduct = shopProductRepository.findByProductProductId(item.getProduct().getProductId());
                
                // Chuyển đổi số lượng sang BigDecimal
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

                // Nhân giá sản phẩm với số lượng
                BigDecimal subtotal = shopProduct.getPrice().multiply(quantity);

                total = total.add(subtotal);

                // Tính phí vận chuyển (giả định khoảng cách 10km)
                BigDecimal feePerItem = shippingFeeRepository.findByWeightAndDistance(item.getProduct().getWeight(), 10.0f)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phí vận chuyển phù hợp"))
                        .getFee();
                shippingFee = shippingFee.add(feePerItem.multiply(quantity));
            }

            // Tính phí hoa hồng (giả định 5%)
            commissionFee = total.multiply(BigDecimal.valueOf(0.05));

            // Xử lý voucher nếu có (áp dụng cho từng đơn hàng của manager)
            if (voucherId != null) {
                Voucher voucher = voucherRepository.findById(voucherId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy voucher với ID: " + voucherId));
                if (voucher.getExpirationDate().isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Voucher đã hết hạn");
                }
                BigDecimal discountPercentage = BigDecimal.valueOf(voucher.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
                discountAmount = total.multiply(discountPercentage);
            }

            total = total.add(shippingFee).add(commissionFee).subtract(discountAmount);

            // Tạo order
            Order order = new Order();
            order.setOrderDate(LocalDate.now());
            order.setShipAddress(shipAddress);
            order.setSenderAddress("Địa chỉ cửa hàng mặc định");
            order.setTotal(total);
            order.setPayment(payment);
            order.setShipper(shipper);
            order.setShippingFee(shippingFee);
            order.setCommissionFee(commissionFee);
            order.setOrderStatus(initialStatus);
            order.setManager(managerRepository.findById(managerId).get());
            order.setCustomer(customer);

            Order savedOrder = orderRepository.save(order);

            // Tạo order details
            for (CartItem item : managerItems) {
                Shop_Product shopProduct = shopProductRepository.findByProductProductId(item.getProduct().getProductId());
                OrderDetail detail = new OrderDetail();
                detail.setOrder(savedOrder);
                detail.setProduct(item.getProduct());
                detail.setQuantity(item.getQuantity());
                detail.setPrice(shopProduct.getPrice()); // Lấy giá từ shop_product

                BigDecimal subtotal = shopProduct.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                detail.setSubtotal(subtotal);

                detail.setWeight(item.getProduct().getWeight());
                detail.setDistance(10.0f);
                detail.setShippingFee(shippingFeeRepository.findByWeightAndDistance(item.getProduct().getWeight(), 10.0f).get());

                orderDetailRepository.save(detail);

                // Giảm số lượng tồn kho
                shopProduct.setQuantity(shopProduct.getQuantity() - item.getQuantity());
                shopProductRepository.save(shopProduct);

                // Xóa cart item sau khi đặt hàng
                cartItemRepository.delete(item);
            }

            // Tạo bản ghi trong order_vouchers nếu dùng voucher
            if (voucherId != null) {
                OrderVoucher orderVoucher = new OrderVoucher();
                orderVoucher.setOrder(savedOrder);
                orderVoucher.setVoucher(voucherRepository.findById(voucherId).get());
                orderVoucher.setDiscountAmount(discountAmount);
                orderVoucher.setTotalAfter(total);
                orderVoucherRepository.save(orderVoucher);
            }

            // Tạo giao dịch
            Transaction transaction = new Transaction();
            transaction.setOrder(savedOrder);
            transaction.setCustomer(customer);
            transaction.setPayment(payment);
            transaction.setAmount(total);
            transaction.setPaymentStatus(PaymentStatus.PENDING);
            transactionRepository.save(transaction);

            // Trả về response cho đơn hàng này
            OrderResponseDTO response = new OrderResponseDTO();
            response.setOrderId(savedOrder.getOrderId());
            response.setTotal(total);
            response.setShippingFee(shippingFee);
            response.setCommissionFee(commissionFee);
            response.setDiscountAmount(discountAmount);
            response.setStatus(savedOrder.getOrderStatus().getName());
            responses.add(response);
        }

        return responses;
    }
}