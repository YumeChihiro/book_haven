package com.haven.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haven.dto.AuthResponse;
import com.haven.dto.LoginRequest;
import com.haven.dto.RegisterRequest;
import com.haven.dto.RegisterResponse;
import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.entity.ManagerAccount;
import com.haven.entity.ManagerAccount.Status;
import com.haven.entity.VerificationToken;
import com.haven.exception.EmailNotVerifiedException;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ManagerAccountRepository;
import com.haven.repository.VerificationTokenRepository;
import com.haven.securities.CustomerUserDetail;
import com.haven.securities.JwtUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private CustomerRepository customerRepository;
    private ManagerAccountRepository managerAccountRepository;
    private VerificationTokenRepository tokenRepository;
    
    public AuthService(AuthenticationManager authenticationManager,
    		JwtUtil jwtUtil,
    		PasswordEncoder passwordEncoder,
    		CustomerRepository customerRepository,
    		VerificationTokenRepository tokenRepository,
    		ManagerAccountRepository managerAccountRepository) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
		this.customerRepository = customerRepository;
		this.tokenRepository = tokenRepository;
		this.managerAccountRepository = managerAccountRepository;
	}


    public AuthResponse authenticate(LoginRequest request) throws BadCredentialsException {
        logger.info("Xử lý yêu cầu đăng nhập - Email: " + request.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        CustomerUserDetail userDetails = (CustomerUserDetail) authentication.getPrincipal();
        
        // Kiểm tra nếu là Customer và chưa verify email
        if (userDetails.getUser() instanceof Customer && !userDetails.isEmailVerified()) {
            throw new EmailNotVerifiedException("Email chưa được xác minh. Vui lòng xác minh email của bạn.");
        }
        
     // Kiểm tra trạng thái tài khoản trong ManagerAccount
        ManagerAccount account = null;
        if (userDetails.getUser() instanceof Manager) {
            account = managerAccountRepository.findByManager((Manager) userDetails.getUser())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ManagerAccount cho Manager"));
        } else if (userDetails.getUser() instanceof Customer) {
            account = managerAccountRepository.findByCustomer((Customer) userDetails.getUser())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ManagerAccount cho Customer"));
        }

        if (account != null && account.getStatus() == Status.Locked) {
            throw new IllegalStateException("Tài khoản của bạn đã bị khóa tạm thời. Vui lòng liên hệ quản trị viên.");
        }
        
        String token;
        if (userDetails.getUser() instanceof Manager) {
            Manager manager = (Manager) userDetails.getUser();
            token = jwtUtil.generateToken(userDetails, manager.getManagerId());
        } else if (userDetails.getUser() instanceof Customer) {
            Customer customer = (Customer) userDetails.getUser();
            token = jwtUtil.generateToken(userDetails, customer.getCustomerId());
        } else {
            throw new RuntimeException("Loại người dùng không hợp lệ");
        }
        
        return new AuthResponse(token);
    }
	
    @Transactional
    public RegisterResponse register(@Valid RegisterRequest request) {
        logger.info("Xử lý yêu cầu đăng ký - Email: {}", request.getEmail());

        // Kiểm tra email đã tồn tại
        if (customerRepository.existsByEmail(request.getEmail())) {
            logger.warn("Đăng ký thất bại - Email đã tồn tại: {}", request.getEmail());
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        // Kiểm tra logic bổ sung (nếu cần)
        if (request.getBirth() != null && request.getBirth().isAfter(LocalDate.now().minusYears(13))) {
            logger.warn("Đăng ký thất bại - Tuổi dưới 13: {}", request.getBirth());
            throw new IllegalArgumentException("Bạn phải từ 13 tuổi trở lên để đăng ký");
        }

        try {
            // Tạo và lưu customer
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setFullName(request.getFullName());
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
            customer.setEmail(request.getEmail());
            customer.setEmailVerified(false);
            customer.setPhone(request.getPhone());
            customer.setBirth(request.getBirth());
            customer.setGender(request.getGender());

            customer = customerRepository.save(customer);
            logger.info("Đăng ký thành công - Customer Name: {}", customer.getName());

            // Tạo tài khoản quản lý cho khách hàng
            ManagerAccount maAccount = new ManagerAccount(
                LocalDate.now(),
                ManagerAccount.Status.Wait,
                null,
                customer
            );
            managerAccountRepository.save(maAccount);

            return new RegisterResponse(
                customer.getEmail(),
                customer.getName(),
                "Đăng ký thành công"
            );

        } catch (Exception e) {
            logger.error("Lỗi khi lưu Customer hoặc ManagerAccount: {}", e.getMessage());
            throw new RuntimeException("Đã có lỗi xảy ra trong quá trình đăng ký: " + e.getMessage(), e);
        }
    }

	 
	 public void sendVerificationEmail(String email) {
	        Customer customer = customerRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với email: " + email));
	            
	        VerificationToken verificationToken = new VerificationToken(customer, null);
	        tokenRepository.save(verificationToken);
	        
	        // Gửi email với OTP
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("Mã xác minh email của bạn");
	        message.setText("Mã xác minh của bạn là: " + verificationToken.getOtp() + 
	                       "\nVui lòng sử dụng mã này trong API để xác minh email. Mã có hiệu lực trong 15 phút.");
	        
	        mailSender.send(message);
	        logger.info("Đã gửi email xác minh với OTP tới: " + email);
	    }
	    
	 @Transactional
	 public boolean verifyEmail(String email, String otp) {
	     try {
	        
	         Customer customer = customerRepository.findByEmail(email)
	             .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với email: " + email));
	         
	         VerificationToken verificationToken = tokenRepository.findByCustomerId(customer.getCustomerId());
	         if (verificationToken == null) {
	             logger.warn("Không tìm thấy OTP cho email: " + email);
	             throw new RuntimeException("OTP không tồn tại hoặc đã bị xóa.");
	         }

	         if (!verificationToken.getOtp().equals(otp)) {
	             logger.warn("OTP không hợp lệ cho email: " + email);
	             throw new RuntimeException("Mã OTP không đúng.");
	         }

	         if (verificationToken.isExpired()) {
	             logger.warn("OTP đã hết hạn cho email: " + email);
	             tokenRepository.delete(verificationToken); // Xóa OTP hết hạn
	             throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng yêu cầu lại.");
	         }

	         customer.setEmailVerified(true);
	         customerRepository.save(customer);

	         tokenRepository.delete(verificationToken);

	         ManagerAccount manaVeri = managerAccountRepository.findByCustomer(customer)
	             .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản quản lý cho customer ID: " + customer.getCustomerId()));
	         manaVeri.setStatus(Status.Confirm);
	         managerAccountRepository.save(manaVeri);

	         logger.info("Email đã được xác minh thành công cho: " + email);
	         return true;
	     } catch (Exception e) {
	         logger.error("Lỗi khi xác minh email {}: {}", email, e.getMessage());
	         throw new RuntimeException("Đã xảy ra lỗi trong quá trình xác minh email: " + e.getMessage());
	     }
	 }

}