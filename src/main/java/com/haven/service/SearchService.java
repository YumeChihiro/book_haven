package com.haven.service;

import com.haven.dto.ProductSearchDTO;
import com.haven.dto.SearchResponseDTO;
import com.haven.dto.ShopSearchDTO;
import com.haven.entity.Customer;
import com.haven.entity.SearchHistory;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ProductRepository;
import com.haven.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    public SearchResponseDTO search(String keyword, Integer customerId) {
        String searchKeyword = "%" + keyword + "%";

     // Lưu lịch sử tìm kiếm
        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setCustomer(customer);
            searchHistory.setKeyword(keyword);
            searchHistory.setSearchedAt(LocalDateTime.now());
            searchHistoryRepository.save(searchHistory);
        }

        // Lấy một shop liên quan nhất
        Object shopResultRaw = productRepository.findTopShopByProductKeyword(searchKeyword);
        ShopSearchDTO shop = null;
        if (shopResultRaw != null) {
            Object[] shopResult = normalizeSingleResult(shopResultRaw);
            if (shopResult != null && shopResult.length > 0) {
                shop = new ShopSearchDTO();
                shop.setManagerId(safeCastToInteger(shopResult[0])); // managerId
                shop.setManagerName((String) shopResult[1]);         // managerName
                shop.setProductCount(((Number) shopResult[2]).longValue()); // productCount
            }
        }

        // Lấy danh sách sản phẩm
        Object productResultsRaw = productRepository.findProductsFromActiveSubscribedShopsNative(searchKeyword);
        List<ProductSearchDTO> products = new ArrayList<>();

        if (productResultsRaw != null) {
            List<Object[]> productResults = normalizeProductResults(productResultsRaw);
            if (!productResults.isEmpty()) {
                products = productResults.stream().map(result -> {
                    ProductSearchDTO dto = new ProductSearchDTO();
                    dto.setProductId(safeCastToInteger(result[0]));           // productId
                    dto.setName((String) result[1]);                          // name
                    dto.setPrice(new BigDecimal(result[2].toString()));       // price
                    dto.setPicture(toPrimitiveByteArray((Byte[]) result[3])); // picture
                    dto.setTotalSold(((Number) result[4]).longValue());       // totalSold
                    return dto;
                }).collect(Collectors.toList());
            }
        }

        // Nếu không có kết quả, dùng truy vấn mặc định với product_id = 1
        if (products.isEmpty()) {
            Object defaultResultRaw = productRepository.findProductDetailsWithSalesByIdNative(1);
            if (defaultResultRaw != null) {
                Object[] defaultResult = normalizeSingleResult(defaultResultRaw);
                if (defaultResult != null && defaultResult.length > 0) {
                    ProductSearchDTO dto = new ProductSearchDTO();
                    dto.setProductId(safeCastToInteger(defaultResult[0]));           // productId
                    dto.setName((String) defaultResult[1]);                          // name
                    dto.setPicture(toPrimitiveByteArray((Byte[]) defaultResult[3])); // picture
                    dto.setPrice(defaultResult[14] != null ? new BigDecimal(defaultResult[14].toString()) : null); // price
                    dto.setTotalSold(((Number) defaultResult[18]).longValue());      // totalSold
                    products.add(dto);
                }
            }
        }

        // Kết hợp kết quả
        SearchResponseDTO response = new SearchResponseDTO();
        response.setShop(shop);
        response.setProducts(products);
        return response;
    }
    
 // Tìm sản phẩm theo thể loại
    public List<ProductSearchDTO> findProductsByCategory(Integer categoryId) {
        List<Object[]> results = productRepository.findProductsByCategoryId(categoryId);
        return results.stream().map(result -> {
            ProductSearchDTO dto = new ProductSearchDTO();
            dto.setProductId(safeCastToInteger(result[0]));           // productId
            dto.setName((String) result[1]);                          // name
            dto.setPrice(new BigDecimal(result[2].toString()));       // price
            dto.setPicture(toPrimitiveByteArray((Byte[]) result[3])); // picture
            dto.setTotalSold(((Number) result[4]).longValue());       // totalSold
            return dto;
        }).collect(Collectors.toList());
    }

    // Tìm sản phẩm theo tác giả
    public List<ProductSearchDTO> findProductsByAuthor(Integer authorId) {
        List<Object[]> results = productRepository.findProductsByAuthorId(authorId);
        return results.stream().map(result -> {
            ProductSearchDTO dto = new ProductSearchDTO();
            dto.setProductId(safeCastToInteger(result[0]));           // productId
            dto.setName((String) result[1]);                          // name
            dto.setPrice(new BigDecimal(result[2].toString()));       // price
            dto.setPicture(toPrimitiveByteArray((Byte[]) result[3])); // picture
            dto.setTotalSold(((Number) result[4]).longValue());       // totalSold
            return dto;
        }).collect(Collectors.toList());
    }

    // Hàm xử lý kết quả đơn
    private Object[] normalizeSingleResult(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Object[]) {
            Object[] array = (Object[]) raw;
            if (array.length == 1 && array[0] instanceof Object[]) {
                return (Object[]) array[0];
            }
            return array;
        }
        return null;
    }

    // Hàm xử lý danh sách kết quả
    private List<Object[]> normalizeProductResults(Object raw) {
        List<Object[]> resultList = new ArrayList<>();
        if (raw instanceof List) {
            List<?> list = (List<?>) raw;
            for (Object item : list) {
                if (item instanceof Object[]) {
                    resultList.add((Object[]) item);
                }
            }
        } else if (raw instanceof Object[]) {
            Object[] array = (Object[]) raw;
            if (array.length > 0 && array[0] instanceof Object[]) {
                resultList.addAll(Arrays.asList((Object[][]) raw));
            } else {
                resultList.add(array);
            }
        }
        return resultList;
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