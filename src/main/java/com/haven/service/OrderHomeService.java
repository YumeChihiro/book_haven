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

@Service
public class OrderHomeService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

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

    @Autowired
    private ProductRepository productRepository;

    // Chức năng mua trực tiếp từ trang chủ
    @Transactional
    public OrderResponseDTO buyProductFromHome(OrderRequestDTO orderRequest) {
        Integer customerId = orderRequest.getCustomerId();
        String shipAddress = orderRequest.getShipAddress();
        Integer paymentId = orderRequest.getPaymentId();
        Integer shipperId = orderRequest.getShipperId();
        Integer voucherId = orderRequest.getVoucherId();
        Integer productId = orderRequest.getProductId();
        Integer quantity = orderRequest.getQuantity();

        // Kiểm tra dữ liệu đầu vào
        if (productId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Product ID và số lượng phải hợp lệ");
        }

        // Kiểm tra thông tin cần thiết
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phương thức thanh toán với ID: " + paymentId));
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shipper với ID: " + shipperId));
        OrderStatus initialStatus = orderStatusRepository.findByName("Pending")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái đơn hàng 'Pending'"));

        // Kiểm tra tồn kho và lấy thông tin từ shop_product
        Shop_Product shopProduct = shopProductRepository.findByProductProductId(productId);
        if (shopProduct == null || shopProduct.getQuantity() < quantity) {
            throw new IllegalArgumentException("Không đủ hàng cho sản phẩm với ID: " + productId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));
        Integer managerId = shopProduct.getManager().getManagerId();

        // Tính toán tổng tiền, phí vận chuyển, và giảm giá từ voucher (nếu có)
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal commissionFee;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Tính phí vận chuyển (giả định khoảng cách 10km)
        BigDecimal quantityShipFee = BigDecimal.valueOf(quantity);
        
        float weight = product.getWeight();
        BigDecimal feePerItem = shippingFeeRepository.findByWeightAndDistance(weight, 10.0f)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phí vận chuyển phù hợp"))
                .getFee();
        shippingFee = shippingFee.add(feePerItem.multiply(quantityShipFee));

        // Tính phí hoa hồng (giả định 5%)
        commissionFee = total.multiply(BigDecimal.valueOf(0.05));

        // Xử lý voucher nếu có
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

        // Tạo order detail
        OrderDetail detail = new OrderDetail();
        detail.setOrder(savedOrder);
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setPrice(shopProduct.getPrice());
        
        BigDecimal subtotal = shopProduct.getPrice().multiply(BigDecimal.valueOf(quantity));
        detail.setSubtotal(subtotal);
        
        detail.setWeight(product.getWeight());
        detail.setDistance(10.0f);
        detail.setShippingFee(shippingFeeRepository.findByWeightAndDistance(weight, 10.0f).get());

        orderDetailRepository.save(detail);

        // Giảm số lượng tồn kho
        shopProduct.setQuantity(shopProduct.getQuantity() - quantity);
        shopProductRepository.save(shopProduct);

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

        // Trả về response
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(savedOrder.getOrderId());
        response.setTotal(total);
        response.setShippingFee(shippingFee);
        response.setCommissionFee(commissionFee);
        response.setDiscountAmount(discountAmount);
        response.setStatus(savedOrder.getOrderStatus().getName());

        return response;
    }
}