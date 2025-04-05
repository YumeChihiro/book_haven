package com.haven.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.entity.VerificationToken;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ManagerRepository;
import com.haven.repository.VerificationTokenRepository;
import com.haven.securities.JwtUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class ChangePasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public void requestOtpForPasswordChange(String token) {
        // Lấy thông tin từ token để tạo UserDetails
        String username = jwtUtil.extractUsername(token);
        Integer userId = jwtUtil.extractUserId(token);
        String roles = jwtUtil.getRolesFromToken(token);

        // Tạo UserDetails giả lập từ token
        UserDetails userDetails = User.withUsername(username)
            .password("") // Không cần mật khẩu vì đã xác thực qua token
            .roles(roles)
            .build();

        // Kiểm tra token với UserDetails
        if (!jwtUtil.validateToken(token, userDetails)) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn");
        }

        Customer customer = null;
        Manager manager = null;
        String email;

        if (roles.contains("CUSTOMER")) {
            customer = customerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Customer với ID: " + userId));
            email = customer.getEmail();
        } else if (roles.contains("SHOP")) {
            manager = managerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Manager với ID: " + userId));
            email = manager.getEmail();
        } else {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }

        VerificationToken verificationToken = new VerificationToken(customer, manager);
        verificationTokenRepository.save(verificationToken);

        sendOtpEmail(email, verificationToken.getOtp());
        logger.info("Đã gửi OTP đến email: " + email);
    }

    @Transactional
    public void changePassword(String token, String oldPassword, String newPassword, String otp) {
        // Lấy thông tin từ token để tạo UserDetails
        String username = jwtUtil.extractUsername(token);
        Integer userId = jwtUtil.extractUserId(token);
        String roles = jwtUtil.getRolesFromToken(token);

        // Tạo UserDetails giả lập từ token
        UserDetails userDetails = User.withUsername(username)
            .password("") // Không cần mật khẩu vì đã xác thực qua token
            .roles(roles)
            .build();

        // Kiểm tra token với UserDetails
        if (!jwtUtil.validateToken(token, userDetails)) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn");
        }

        Customer customer = null;
        Manager manager = null;

        if (roles.contains("CUSTOMER")) {
            customer = customerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Customer với ID: " + userId));
            if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
                throw new IllegalArgumentException("Mật khẩu cũ không đúng");
            }
            VerificationToken verificationToken = verificationTokenRepository.findByOtpAndCustomerCustomerId(otp, userId)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ"));
            if (verificationToken.isExpired()) {
                throw new IllegalArgumentException("OTP đã hết hạn");
            }
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerRepository.save(customer);
            verificationTokenRepository.delete(verificationToken);
            logger.info("Đã đổi mật khẩu cho Customer: " + customer.getEmail());
        } else if (roles.contains("SHOP")) {
            manager = managerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Manager với ID: " + userId));
            if (!passwordEncoder.matches(oldPassword, manager.getPassword())) {
                throw new IllegalArgumentException("Mật khẩu cũ không đúng");
            }
            VerificationToken verificationToken = verificationTokenRepository.findByOtpAndManagerManagerId(otp, userId)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ"));
            if (verificationToken.isExpired()) {
                throw new IllegalArgumentException("OTP đã hết hạn");
            }
            manager.setPassword(passwordEncoder.encode(newPassword));
            managerRepository.save(manager);
            verificationTokenRepository.delete(verificationToken);
            logger.info("Đã đổi mật khẩu cho Manager: " + manager.getEmail());
        } else {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Mã OTP để đổi mật khẩu");
            helper.setText("Mã OTP của bạn là: <strong>" + otp + "</strong>. Mã này có hiệu lực trong 15 phút.", true);
            mailSender.send(message);
            logger.info("Đã gửi email OTP đến: " + toEmail);
        } catch (MessagingException e) {
            logger.error("Lỗi khi gửi email OTP: " + e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email OTP");
        }
    }
}