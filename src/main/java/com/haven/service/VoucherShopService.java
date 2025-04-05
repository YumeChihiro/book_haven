package com.haven.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.VoucherDTO;
import com.haven.entity.CartItem;
import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.entity.Message;
import com.haven.entity.Order;
import com.haven.entity.Voucher;
import com.haven.repository.CartItemRepository;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ManagerRepository;
import com.haven.repository.MessageRepository;
import com.haven.repository.OrderRepository;
import com.haven.repository.VoucherRepository;

@Service
public class VoucherShopService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    // Lấy danh sách vouchers từ messages của shop
    public List<VoucherDTO> getVouchersForShop(Integer shopManagerId) {
        List<Message> messages = messageRepository.findByManagerManagerId(shopManagerId);
        List<VoucherDTO> vouchers = new ArrayList<>();
        Pattern pattern = Pattern.compile(
            "Voucher Code: (\\w+)\\nDiscount: (\\d+\\.\\d+)%\\nExpiration Date: (\\d{4}-\\d{2}-\\d{2})"
        );

        for (Message message : messages) {
            if (message.getTitle().equals("New Voucher Available")) {
                Matcher matcher = pattern.matcher(message.getContent());
                if (matcher.find()) {
                    VoucherDTO voucher = new VoucherDTO();
                    voucher.setVoucherCode(matcher.group(1));
                    voucher.setDiscountPercentage(Float.parseFloat(matcher.group(2)));
                    voucher.setExpirationDate(LocalDate.parse(matcher.group(3)));
                    vouchers.add(voucher);
                }
            }
        }
        return vouchers;
    }

    // Gửi voucher cho một customer dựa trên giỏ hàng
    @Transactional
    public Message sendVoucherToCustomerByCart(Integer shopId, Integer customerId, String voucherCode) {
        Manager shop = managerRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));

        // Kiểm tra hạn sử dụng voucher
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        if (voucher.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }

        // Điều kiện: Kiểm tra cart có sản phẩm của shop không
        List<CartItem> cartItems = cartItemRepository.findByCustomerIdAndShopId(customerId, shopId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của customer không chứa sản phẩm của shop");
        }

        String title = "New Voucher From Shop";
        String content = String.format(
            "Bạn nhận được voucher từ shop vì có sản phẩm trong giỏ hàng:\n" +
            "Voucher Code: %s\n" +
            "Discount: %.2f%%\n" +
            "Hạn sử dụng: %s",
            voucherCode, voucher.getDiscountPercentage(), voucher.getExpirationDate()
        );

        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setSender(shop);
        message.setCustomer(customer);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.save(message);
    }

    // Gửi voucher cho một customer dựa trên đơn hàng hoàn thành
    @Transactional
    public Message sendVoucherToCustomerByOrder(Integer shopId, Integer customerId, String voucherCode) {
        Manager shop = managerRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));

        // Kiểm tra hạn sử dụng voucher
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        if (voucher.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }

        // Điều kiện: Kiểm tra có order thành công không
        List<Order> successfulOrders = orderRepository.findByManagerManagerIdAndOrderStatusName(shopId, "SUCCESS");
        
        boolean hasSuccessfulOrder = successfulOrders.stream()
                .anyMatch(order -> ((Integer) order.getCustomer().getCustomerId()).equals(customerId));
        if (!hasSuccessfulOrder) {
            throw new RuntimeException("Customer chưa có đơn hàng thành công với shop");
        }

        String title = "New Voucher From Shop";
        String content = String.format(
            "Bạn nhận được voucher từ shop vì đã có đơn hàng thành công:\n" +
            "Voucher Code: %s\n" +
            "Discount: %.2f%%\n" +
            "Hạn sử dụng: %s",
            voucherCode, voucher.getDiscountPercentage(), voucher.getExpirationDate()
        );

        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setSender(shop);
        message.setCustomer(customer);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.save(message);
    }

    // Gửi voucher cho tất cả customer có sản phẩm trong giỏ hàng
    @Transactional
    public List<Message> sendVoucherToAllCustomersByCart(Integer shopId, String voucherCode) {
        Manager shop = managerRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));

        // Kiểm tra hạn sử dụng voucher
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        if (voucher.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }

        // Lấy tất cả customer có sản phẩm của shop trong giỏ hàng
        List<CartItem> allCartItems = cartItemRepository.findByCustomerIdAndShopId(null, shopId);
        Set<Integer> customerIds = allCartItems.stream()
                .map(cartItem -> ((Integer) cartItem.getCart().getCustomer().getCustomerId()))
                .collect(Collectors.toSet());

        List<Message> sentMessages = new ArrayList<>();
        for (Integer customerId : customerIds) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));

            String title = "New Voucher From Shop";
            String content = String.format(
                "Bạn nhận được voucher từ shop vì có sản phẩm trong giỏ hàng:\n" +
                "Voucher Code: %s\n" +
                "Discount: %.2f%%\n" +
                "Hạn sử dụng: %s",
                voucherCode, voucher.getDiscountPercentage(), voucher.getExpirationDate()
            );

            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setSender(shop);
            message.setCustomer(customer);
            message.setSentAt(LocalDateTime.now());
            message.setRead(false);

            sentMessages.add(messageRepository.save(message));
        }

        if (sentMessages.isEmpty()) {
            throw new RuntimeException("Không có customer nào có sản phẩm của shop trong giỏ hàng");
        }

        return sentMessages;
    }

    // Gửi voucher cho tất cả customer có đơn hàng hoàn thành
    @Transactional
    public List<Message> sendVoucherToAllCustomersByOrder(Integer shopId, String voucherCode) {
        Manager shop = managerRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));

        // Kiểm tra hạn sử dụng voucher
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        if (voucher.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }

        // Lấy tất cả customer có order thành công với shop
        List<Order> successfulOrders = orderRepository.findByManagerManagerIdAndOrderStatusName(shopId, "SUCCESS");
        Set<Integer> customerIds = successfulOrders.stream()
                .map(order -> order.getCustomer().getCustomerId())
                .collect(Collectors.toSet());

        List<Message> sentMessages = new ArrayList<>();
        for (Integer customerId : customerIds) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));

            String title = "New Voucher From Shop";
            String content = String.format(
                "Bạn nhận được voucher từ shop vì đã có đơn hàng thành công:\n" +
                "Voucher Code: %s\n" +
                "Discount: %.2f%%\n" +
                "Hạn sử dụng: %s",
                voucherCode, voucher.getDiscountPercentage(), voucher.getExpirationDate()
            );

            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setSender(shop);
            message.setCustomer(customer);
            message.setSentAt(LocalDateTime.now());
            message.setRead(false);

            sentMessages.add(messageRepository.save(message));
        }

        if (sentMessages.isEmpty()) {
            throw new RuntimeException("Không có customer nào có đơn hàng thành công với shop");
        }

        return sentMessages;
    }
}