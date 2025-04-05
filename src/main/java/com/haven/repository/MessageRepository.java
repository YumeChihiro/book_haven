package com.haven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByCustomerCustomerId(Integer customerId);
    List<Message> findByManagerManagerId(Integer managerId);
    List<Message> findBySenderManagerId(Integer managerId);
}