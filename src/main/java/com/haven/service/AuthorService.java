package com.haven.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.AuthorDTO;
import com.haven.entity.Author;
import com.haven.repository.AuthorRepository;

@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Create
    @Transactional
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        mapDtoToEntity(authorDTO, author);
        Author savedAuthor = authorRepository.save(author);
        logger.info("Tác giả mới đã được tạo: authorId={}", savedAuthor.getAuthorId());
        return mapEntityToDto(savedAuthor);
    }

    // Read (Get by ID)
    public AuthorDTO getAuthorById(Integer authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + authorId));
        return mapEntityToDto(author);
    }

    // Read (Get all)
    public List<AuthorDTO> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    // Update
    @Transactional
    public AuthorDTO updateAuthor(Integer authorId, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + authorId));
        mapDtoToEntity(authorDTO, author);
        Author updatedAuthor = authorRepository.save(author);
        logger.info("Tác giả đã được cập nhật: authorId={}", updatedAuthor.getAuthorId());
        return mapEntityToDto(updatedAuthor);
    }

    // Delete
    @Transactional
    public void deleteAuthor(Integer authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID: " + authorId));
        authorRepository.delete(author);
        logger.info("Tác giả đã bị xóa: authorId={}", authorId);
    }

    // Helper methods
    private void mapDtoToEntity(AuthorDTO dto, Author entity) {
        entity.setName(dto.getName());
        entity.setBirth(dto.getBirth());
        entity.setNationality(dto.getNationality());
        entity.setDescription(dto.getDescription());
    }

    private AuthorDTO mapEntityToDto(Author entity) {
        return new AuthorDTO(
            entity.getAuthorId(),
            entity.getName(),
            entity.getBirth(),
            entity.getNationality(),
            entity.getDescription()
        );
    }
}