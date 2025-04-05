package com.haven.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.entity.CartItem;
import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.entity.Message;
import com.haven.entity.ShopSubscription;
import com.haven.entity.SubscriptionStatus;
import com.haven.repository.CartItemRepository;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ManagerRepository;
import com.haven.repository.MessageRepository;
import com.haven.repository.ShopSubscriptionRepository;

@Service
public class MessageNoReplyService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShopSubscriptionRepository shopSubscriptionRepository;

    @Transactional
    public Message sendMessageToCustomer(Integer senderManagerId, Integer customerId, String title, String content) {
        Manager sender = managerRepository.findById(senderManagerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));

        // Kiểm tra giỏ hàng của customer có sản phẩm của shop không
        List<CartItem> cartItems = cartItemRepository.findByCustomerIdAndShopId(customerId, senderManagerId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của customer không chứa sản phẩm của shop");
        }

        // Kiểm tra gói đăng ký của shop, loại trừ gói "BASIC"
        List<ShopSubscription> subscriptions = shopSubscriptionRepository.findByManagerManagerId(senderManagerId);
        boolean hasBasicPlan = subscriptions.stream()
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE)
                .anyMatch(sub -> sub.getPlan().getPlanName().equals("Cơ bản"));
        if (hasBasicPlan) {
            throw new RuntimeException("Shop có gói cơ bản không được phép gửi message");
        }

        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setSender(sender);
        message.setCustomer(customer);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.save(message);
    }

    @Transactional
    public Message sendMessageToShop(Integer senderManagerId, Integer managerId, String title, String content) {
        Manager sender = managerRepository.findById(senderManagerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Admin"));
        
        Manager shop = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shop"));

        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setSender(sender);
        message.setManager(shop);

        return messageRepository.save(message);
    }

    public List<Message> getCustomerMessages(Integer customerId) {
        return messageRepository.findByCustomerCustomerId(customerId);
    }

    public List<Message> getShopMessages(Integer managerId) {
        return messageRepository.findByManagerManagerId(managerId);
    }

    @Transactional
    public void markAsRead(Integer messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));
        message.setRead(true);
        messageRepository.save(message);
    }

    public Message getMessageById(Integer messageId) {
        return messageRepository.findById(messageId)
                .orElse(null);
    }
}