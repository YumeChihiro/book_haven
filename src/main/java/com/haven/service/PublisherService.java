package com.haven.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.PublisherDTO;
import com.haven.entity.Publisher;
import com.haven.repository.PublisherRepository;

@Service
public class PublisherService {

    private static final Logger logger = LoggerFactory.getLogger(PublisherService.class);

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional
    public PublisherDTO createPublisher(PublisherDTO publisherDTO) {
        Publisher publisher = new Publisher();
        mapDtoToEntity(publisherDTO, publisher);
        Publisher savedPublisher = publisherRepository.save(publisher);
        logger.info("Nhà xuất bản mới đã được tạo: publisherId={}", savedPublisher.getPublisherId());
        return mapEntityToDto(savedPublisher);
    }

    public PublisherDTO getPublisherById(Integer publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà xuất bản với ID: " + publisherId));
        return mapEntityToDto(publisher);
    }

    public List<PublisherDTO> getAllPublishers() {
        List<Publisher> publishers = publisherRepository.findAll();
        return publishers.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PublisherDTO updatePublisher(Integer publisherId, PublisherDTO publisherDTO) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà xuất bản với ID: " + publisherId));
        mapDtoToEntity(publisherDTO, publisher);
        Publisher updatedPublisher = publisherRepository.save(publisher);
        logger.info("Nhà xuất bản đã được cập nhật: publisherId={}", updatedPublisher.getPublisherId());
        return mapEntityToDto(updatedPublisher);
    }

    @Transactional
    public void deletePublisher(Integer publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà xuất bản với ID: " + publisherId));
        publisherRepository.delete(publisher);
        logger.info("Nhà xuất bản đã bị xóa: publisherId={}", publisherId);
    }

    private void mapDtoToEntity(PublisherDTO dto, Publisher entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
    }

    private PublisherDTO mapEntityToDto(Publisher entity) {
        return new PublisherDTO(
            entity.getPublisherId(),
            entity.getName(),
            entity.getDescription()
        );
    }
}