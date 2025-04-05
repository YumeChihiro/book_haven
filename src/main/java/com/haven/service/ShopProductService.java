package com.haven.service;

import com.haven.dto.ProductRequestDTO;
import com.haven.dto.ProductResponseDTO;
import com.haven.entity.*;
import com.haven.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private ProductResponseDTO convertToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPicture(product.getPicture());
        dto.setWeight(product.getWeight());
        
        dto.setAuthor(new ProductResponseDTO.AuthorDTO(
            product.getAuthor().getAuthorId(),
            product.getAuthor().getName()
        ));
        
        dto.setCategory(new ProductResponseDTO.CategoryDTO(
            product.getCategory().getCategoryId(),
            product.getCategory().getName()
        ));
        
        dto.setPublisher(new ProductResponseDTO.PublisherDTO(
            product.getPublisher().getPublisherId(),
            product.getPublisher().getName()
        ));
        
        List<ProductResponseDTO.ShopProductDTO> shopProductDTOs = product.getShopProducts().stream()
            .map(sp -> new ProductResponseDTO.ShopProductDTO(
                sp.getId(),
                sp.getPrice(),
                sp.getQuantity()
            ))
            .collect(Collectors.toList());
        dto.setShopProducts(shopProductDTOs);
        
        return dto;
    }

    // Read: Lấy tất cả sản phẩm của shop hiện tại
    public List<ProductResponseDTO> getAllProducts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

        List<Shop_Product> shopProducts = shopProductRepository.findByManagerManagerId(manager.getManagerId());
        
        return shopProducts.stream()
            .map(Shop_Product::getProduct)
            .distinct()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Read: Lấy sản phẩm theo ID (chỉ nếu thuộc shop hiện tại)
    public ProductResponseDTO getProductById(int productId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean belongsToShop = shopProductRepository.findByManagerManagerId(manager.getManagerId())
            .stream()
            .anyMatch(sp -> sp.getProduct().getProductId() == productId);

        if (!belongsToShop) {
            throw new RuntimeException("Product does not belong to this shop");
        }

        return convertToDTO(product);
    }

    // Create: Thêm sản phẩm mới
    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

        Optional<Author> author = authorRepository.findById(dto.getAuthorId());
        Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
        Optional<Publisher> publisher = publisherRepository.findById(dto.getPublisherId());

        if (author.isPresent() && category.isPresent() && publisher.isPresent()) {
            Product product = new Product();
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPicture(dto.getPicture());
            product.setWeight(dto.getWeight());
            product.setAuthor(author.get());
            product.setCategory(category.get());
            product.setPublisher(publisher.get());

            Product savedProduct = productRepository.save(product);

            Shop_Product shopProduct = new Shop_Product();
            shopProduct.setManager(manager);
            shopProduct.setProduct(savedProduct);
            shopProduct.setPrice(dto.getPrice());
            shopProduct.setQuantity(dto.getQuantity());
            shopProductRepository.save(shopProduct);

            savedProduct.getShopProducts().add(shopProduct);

            return convertToDTO(savedProduct);
        }
        throw new RuntimeException("Author, Category or Publisher not found");
    }

    // Update: Sửa thông tin sản phẩm
    @Transactional
    public ProductResponseDTO updateProduct(int productId, ProductRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<Author> author = authorRepository.findById(dto.getAuthorId());
        Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
        Optional<Publisher> publisher = publisherRepository.findById(dto.getPublisherId());

        if (productOpt.isPresent() && author.isPresent() && category.isPresent() && publisher.isPresent()) {
            Product product = productOpt.get();
            
            boolean belongsToShop = shopProductRepository.findByManagerManagerId(manager.getManagerId())
                .stream()
                .anyMatch(sp -> sp.getProduct().getProductId() == productId);

            if (!belongsToShop) {
                throw new RuntimeException("Product does not belong to this shop");
            }

            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPicture(dto.getPicture());
            product.setWeight(dto.getWeight());
            product.setAuthor(author.get());
            product.setCategory(category.get());
            product.setPublisher(publisher.get());
            
            Product updatedProduct = productRepository.save(product);
            return convertToDTO(updatedProduct);
        }
        throw new RuntimeException("Product, Author, Category or Publisher not found");
    }

    // Delete: Xóa sản phẩm
    @Transactional
    public void deleteProduct(int productId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            
            boolean belongsToShop = shopProductRepository.findByManagerManagerId(manager.getManagerId())
                .stream()
                .anyMatch(sp -> sp.getProduct().getProductId() == productId);

            if (!belongsToShop) {
                throw new RuntimeException("Product does not belong to this shop");
            }

            productRepository.delete(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }
}