package com.haven.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.CategoryDTO;
import com.haven.entity.Category;
import com.haven.repository.CategoryRepository;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        mapDtoToEntity(categoryDTO, category);
        Category savedCategory = categoryRepository.save(category);
        logger.info("Danh mục mới đã được tạo: categoryId={}", savedCategory.getCategoryId());
        return mapEntityToDto(savedCategory);
    }

    public CategoryDTO getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        return mapEntityToDto(category);
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO updateCategory(Integer categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        mapDtoToEntity(categoryDTO, category);
        Category updatedCategory = categoryRepository.save(category);
        logger.info("Danh mục đã được cập nhật: categoryId={}", updatedCategory.getCategoryId());
        return mapEntityToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        categoryRepository.delete(category);
        logger.info("Danh mục đã bị xóa: categoryId={}", categoryId);
    }

    private void mapDtoToEntity(CategoryDTO dto, Category entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescriptionCategory());
    }

    private CategoryDTO mapEntityToDto(Category entity) {
        return new CategoryDTO(
            entity.getName(),
            entity.getDescription()
        );
    }
}