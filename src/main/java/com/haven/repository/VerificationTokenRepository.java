package com.haven.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haven.entity.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByOtp(String otp);
    
    @Query("SELECT v FROM VerificationToken v WHERE v.customer.customerId = :customerId")
    VerificationToken findByCustomerId(@Param("customerId") int customerId);
    
    Optional<VerificationToken> findByOtpAndManagerManagerId(String otp, Integer managerId);
    Optional<VerificationToken> findByOtpAndCustomerCustomerId(String otp, Integer customerId);

}