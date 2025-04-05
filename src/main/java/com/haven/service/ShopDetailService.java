package com.haven.service;

import com.haven.dto.ProductSearchDTO;
import com.haven.dto.ShopDetailsDTO;
import com.haven.entity.Manager;
import com.haven.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopDetailService {

    @Autowired
    private ManagerRepository managerRepository;

    public ShopDetailsDTO getShopDetails(Integer managerId) {
        // Lấy thông tin cơ bản của shop
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found with ID: " + managerId));

        // Tạo DTO
        ShopDetailsDTO shopDetails = new ShopDetailsDTO();
        shopDetails.setManagerId(manager.getManagerId());
        shopDetails.setManagerName(manager.getManagerName());

        // Thống kê số lượng sản phẩm hiện có
        Long availableProducts = managerRepository.countAvailableProducts(managerId);
        shopDetails.setAvailableProducts(availableProducts != null ? availableProducts : 0);

        // Thống kê số lượng sản phẩm đã bán
        Long totalSoldProducts = managerRepository.sumTotalSoldProducts(managerId);
        shopDetails.setTotalSoldProducts(totalSoldProducts != null ? totalSoldProducts : 0);

        // Thống kê điểm đánh giá trung bình
        Double averageRating = managerRepository.getAverageRating(managerId);
        shopDetails.setAverageRating(averageRating != null ? averageRating : 0.0);

        // Lấy danh sách sản phẩm
        List<Object[]> productResults = managerRepository.findProductsByManagerId(managerId);
        List<ProductSearchDTO> products = productResults.stream().map(result -> {
            ProductSearchDTO dto = new ProductSearchDTO();
            dto.setProductId(safeCastToInteger(result[0]));           // productId
            dto.setName((String) result[1]);                          // name
            dto.setPrice(new BigDecimal(result[2].toString()));       // price
            dto.setPicture(toPrimitiveByteArray((Byte[]) result[3])); // picture
            dto.setTotalSold(((Number) result[4]).longValue());       // totalSold
            return dto;
        }).collect(Collectors.toList());
        shopDetails.setProducts(products);

        return shopDetails;
    }

    private Integer safeCastToInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        throw new IllegalStateException("Cannot cast " + obj.getClass().getName() + " to Integer");
    }

    private byte[] toPrimitiveByteArray(Byte[] boxedArray) {
        if (boxedArray == null) return null;
        byte[] primitiveArray = new byte[boxedArray.length];
        for (int i = 0; i < boxedArray.length; i++) {
            primitiveArray[i] = boxedArray[i] != null ? boxedArray[i] : 0;
        }
        return primitiveArray;
    }
}