package com.haven.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer>{

}
