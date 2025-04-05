package com.haven.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.Publisher;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer>{

}
