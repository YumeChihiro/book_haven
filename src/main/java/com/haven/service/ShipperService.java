package com.haven.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.ShipperDTO;
import com.haven.entity.Shipper;
import com.haven.repository.ShipperRepository;

@Service
public class ShipperService {

    private static final Logger logger = LoggerFactory.getLogger(ShipperService.class);

    private final ShipperRepository shipperRepository;

    public ShipperService(ShipperRepository shipperRepository) {
        this.shipperRepository = shipperRepository;
    }

    @Transactional
    public ShipperDTO createShipper(ShipperDTO shipperDTO) {
        Shipper shipper = new Shipper();
        mapDtoToEntity(shipperDTO, shipper);
        Shipper savedShipper = shipperRepository.save(shipper);
        logger.info("Shipper mới đã được tạo: shipperId={}", savedShipper.getShipperId());
        return mapEntityToDto(savedShipper);
    }

    public ShipperDTO getShipperById(Integer shipperId) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + shipperId));
        return mapEntityToDto(shipper);
    }

    public List<ShipperDTO> getAllShippers() {
        List<Shipper> shippers = shipperRepository.findAll();
        return shippers.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShipperDTO updateShipper(Integer shipperId, ShipperDTO shipperDTO) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + shipperId));
        mapDtoToEntity(shipperDTO, shipper);
        Shipper updatedShipper = shipperRepository.save(shipper);
        logger.info("Shipper đã được cập nhật: shipperId={}", updatedShipper.getShipperId());
        return mapEntityToDto(updatedShipper);
    }

    @Transactional
    public void deleteShipper(Integer shipperId) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + shipperId));
        shipperRepository.delete(shipper);
        logger.info("Shipper đã bị xóa: shipperId={}", shipperId);
    }

    private void mapDtoToEntity(ShipperDTO dto, Shipper entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
    }

    private ShipperDTO mapEntityToDto(Shipper entity) {
        return new ShipperDTO(
            entity.getShipperId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone()
        );
    }
}