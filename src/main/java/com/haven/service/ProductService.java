package com.haven.service;

import com.haven.dto.OneProductDetailsDTO;
import com.haven.dto.ProductDetailsDTO;
import com.haven.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    
    public List<ProductDetailsDTO> getProductsFromActiveSubscribedShops() {
        List<Object[]> results = productRepository.findProductsFromActiveSubscribedShopsNative(); 
        
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }
        
        return results.stream()
                .map(result -> new ProductDetailsDTO(
                        (Integer) result[0], // productId
                        (String) result[1],  // name
                        (BigDecimal) result[2], // price
                        (byte[]) result[3],  // picture
                        ((Number) result[4]).longValue() // totalSold
                ))
                .collect(Collectors.toList());
    }
    
    public OneProductDetailsDTO getProductDetailsWithSalesById(Integer productId) {
        Object[] result = productRepository.findProductDetailsWithSalesByIdNative(productId);
        if (result == null || result.length == 0) {
            return null;
        }

        // Kiểm tra xem result[0] có phải là một mảng Object[] không
        if (!(result[0] instanceof Object[])) {
            throw new IllegalStateException("Expected result[0] to be an Object[] but got " + result[0].getClass().getName());
        }

        // Lấy dữ liệu từ result[0]
        Object[] row = (Object[]) result[0];

        // Ép kiểu an toàn cho các cột Integer
        Integer productIdResult = safeCastToInteger(row[0]);
        Integer publisherId = safeCastToInteger(row[5]);
        Integer authorId = safeCastToInteger(row[7]);
        Integer categoryId = safeCastToInteger(row[9]);
        Integer shopProductId = safeCastToInteger(row[11]);
        Integer shopProductManagerId = safeCastToInteger(row[12]);
        Integer shopProductProductId = safeCastToInteger(row[13]);
        Integer quantity = safeCastToInteger(row[15]);
        Integer managerId = safeCastToInteger(row[16]);

        return new OneProductDetailsDTO(
                productIdResult, // productId
                (String) row[1],  // name
                (String) row[2],  // description
                toPrimitiveByteArray((Byte[]) row[3]), // picture
                (BigDecimal) row[4], // weight
                publisherId, // publisherId
                (String) row[6],  // publisherName
                authorId, // authorId
                (String) row[8],  // authorName
                categoryId, // categoryId
                (String) row[10], // categoryName
                shopProductId, // shopProductId
                shopProductManagerId, // shopProductManagerId
                shopProductProductId, // shopProductProductId
                (BigDecimal) row[14], // price
                quantity, // quantity
                (String) row[17],  // managerName
                ((Number) row[18]).longValue() // totalSold
        );
    }
 // Chuyển Byte[] thành byte[]
    private byte[] toPrimitiveByteArray(Byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        byte[] result = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            result[i] = byteArray[i];
        }
        return result;
    }
    
    private Integer safeCastToInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        throw new ClassCastException("Cannot cast " + obj.getClass().getName() + " to Integer");
    }
}