package com.haven.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
