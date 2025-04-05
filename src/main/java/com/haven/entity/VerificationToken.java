package com.haven.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String otp;
    
    @OneToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = true)
    private Customer customer;
    
    @OneToOne(targetEntity = Manager.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id", referencedColumnName = "manager_id", nullable = true)
    private Manager manager;
    
    private LocalDateTime expiryDate;
    
    public VerificationToken() {}
    
    public VerificationToken(Customer customer, Manager manager) {
        if (customer != null) {
            this.customer = customer;
        } else if (manager != null) {
            this.manager = manager;
        } else {
            throw new IllegalArgumentException("Phải cung cấp Customer hoặc Manager");
        }
        this.otp = String.format("%06d", (int)(Math.random() * 1000000)); // Tạo OTP 6 số
        this.expiryDate = LocalDateTime.now().plusMinutes(15); // Hết hạn sau 15 phút
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public String getOtp() { return otp; }
    public Customer getCustomer() { return customer; }
    
    public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public LocalDateTime getExpiryDate() { return expiryDate; }
    
    public void setId(Long id) { this.id = id; }
    public void setOtp(String otp) { this.otp = otp; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}