package com.haven.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.OrderVoucher;

@Repository
public interface OrderVoucherRepository extends JpaRepository<OrderVoucher, Integer>{
	 Optional<OrderVoucher> findByOrderOrderId(Integer orderId);
}
