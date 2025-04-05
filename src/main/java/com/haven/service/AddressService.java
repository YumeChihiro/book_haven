package com.haven.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haven.dto.AddressDTO;
import com.haven.entity.Address;
import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.repository.AddressRepository;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ManagerRepository;

@Service
public class AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;

    public AddressService(AddressRepository addressRepository,
                          ManagerRepository managerRepository,
                          CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.managerRepository = managerRepository;
        this.customerRepository = customerRepository;
    }

    // Create
    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO, Integer userId, boolean isManager) {
        Address address = new Address();
        mapDtoToEntity(addressDTO, address);

        if (isManager) {
            Manager manager = managerRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Manager"));
            address.setManager(manager);
            address.setCustomer(null);
        } else {
            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer"));
            address.setCustomer(customer);
            address.setManager(null);
        }

        Address savedAddress = addressRepository.save(address);
        logger.info("Địa chỉ mới đã được tạo: addressId={}, userId={}, isManager={}", 
                    savedAddress.getAddressId(), userId, isManager);
        return mapEntityToDto(savedAddress);
    }

    // Read (Get by ID)
    public AddressDTO getAddressById(Integer addressId, Integer userId, boolean isManager) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với ID: " + addressId));
        checkOwnership(address, userId, isManager);
        return mapEntityToDto(address);
    }

    // Read (Get all by user)
    public List<AddressDTO> getAllAddressesByUser(Integer userId, boolean isManager) {
        List<Address> addresses = isManager 
                ? addressRepository.findByManagerManagerId(userId)
                : addressRepository.findByCustomerCustomerId(userId);
        return addresses.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    // Update
    @Transactional
    public AddressDTO updateAddress(Integer addressId, AddressDTO addressDTO, Integer userId, boolean isManager) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với ID: " + addressId));
        checkOwnership(address, userId, isManager);
        mapDtoToEntity(addressDTO, address);
        Address updatedAddress = addressRepository.save(address);
        logger.info("Địa chỉ đã được cập nhật: addressId={}, userId={}, isManager={}", 
                    updatedAddress.getAddressId(), userId, isManager);
        return mapEntityToDto(updatedAddress);
    }

    // Delete
    @Transactional
    public void deleteAddress(Integer addressId, Integer userId, boolean isManager) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với ID: " + addressId));
        checkOwnership(address, userId, isManager);
        addressRepository.delete(address);
        logger.info("Địa chỉ đã bị xóa: addressId={}, userId={}, isManager={}", 
                    addressId, userId, isManager);
    }

    // Helper methods
    private void mapDtoToEntity(AddressDTO dto, Address entity) {
        entity.setRoad(dto.getRoad());
        entity.setTown(dto.getTown());
        entity.setDistrict(dto.getDistrict());
        entity.setProvince(dto.getProvince());
        entity.setType(dto.getType());
    }

    private AddressDTO mapEntityToDto(Address entity) {
        return new AddressDTO(
            entity.getRoad(),
            entity.getTown(),
            entity.getDistrict(),
            entity.getProvince(),
            entity.getType()
        );
    }

    private void checkOwnership(Address address, Integer userId, boolean isManager) {
        if (isManager) {
            if (address.getManager() == null || !(address.getManager().getManagerId() == userId)) {
                throw new IllegalStateException("Bạn không có quyền truy cập địa chỉ này.");
            }
        } else {
            if (address.getCustomer() == null || !(address.getCustomer().getCustomerId() == userId)) {
                throw new IllegalStateException("Bạn không có quyền truy cập địa chỉ này.");
            }
        }
    }
}