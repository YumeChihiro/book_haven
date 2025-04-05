package com.haven.dto;

import java.math.BigDecimal;

public class OneProductDetailsDTO {

    private Integer productId;
    private String name;
    private String description;
    private byte[] picture;
    private BigDecimal weight;
    private PublisherDTO publisher;
    private AuthorDTO author;
    private CategoryDTO category;
    private ShopProductDTO shopProduct;
    private Long totalSold;

    public OneProductDetailsDTO(Integer productId, String name, String description, byte[] picture, BigDecimal weight,
                             Integer publisherId, String publisherName, Integer authorId, String authorName,
                             Integer categoryId, String categoryName, Integer shopProductId, Integer managerId,
                             Integer shopProductProductId, BigDecimal price, Integer quantity, String managerName,
                             Long totalSold) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.weight = weight;
        this.publisher = new PublisherDTO(publisherId, publisherName);
        this.author = new AuthorDTO(authorId, authorName);
        this.category = new CategoryDTO(categoryId, categoryName);
        this.shopProduct = new ShopProductDTO(shopProductId, managerId, shopProductProductId, price, quantity, new ManagerDTO(managerId, managerName));
        this.totalSold = totalSold;
    }

    // Getters và Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public PublisherDTO getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherDTO publisher) {
        this.publisher = publisher;
    }

    public AuthorDTO getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public ShopProductDTO getShopProduct() {
        return shopProduct;
    }

    public void setShopProduct(ShopProductDTO shopProduct) {
        this.shopProduct = shopProduct;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Long totalSold) {
        this.totalSold = totalSold;
    }

    // DTO con cho Publisher
    public static class PublisherDTO {
        private Integer publisherId;
        private String name;

        public PublisherDTO(Integer publisherId, String name) {
            this.publisherId = publisherId;
            this.name = name;
        }

        public Integer getPublisherId() {
            return publisherId;
        }

        public void setPublisherId(Integer publisherId) {
            this.publisherId = publisherId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // DTO con cho Author
    public static class AuthorDTO {
        private Integer authorId;
        private String name;

        public AuthorDTO(Integer authorId, String name) {
            this.authorId = authorId;
            this.name = name;
        }

        public Integer getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Integer authorId) {
            this.authorId = authorId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // DTO con cho Category
    public static class CategoryDTO {
        private Integer categoryId;
        private String name;

        public CategoryDTO(Integer categoryId, String name) {
            this.categoryId = categoryId;
            this.name = name;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // DTO con cho ShopProduct
    public static class ShopProductDTO {
        private Integer id;
        private Integer managerId;
        private Integer productId;
        private BigDecimal price;
        private Integer quantity;
        private ManagerDTO manager;

        public ShopProductDTO(Integer id, Integer managerId, Integer productId, BigDecimal price, Integer quantity, ManagerDTO manager) {
            this.id = id;
            this.managerId = managerId;
            this.productId = productId;
            this.price = price;
            this.quantity = quantity;
            this.manager = manager;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getManagerId() {
            return managerId;
        }

        public void setManagerId(Integer managerId) {
            this.managerId = managerId;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public ManagerDTO getManager() {
            return manager;
        }

        public void setManager(ManagerDTO manager) {
            this.manager = manager;
        }
    }

    // DTO con cho Manager
    public static class ManagerDTO {
        private Integer managerId;
        private String name;

        public ManagerDTO(Integer managerId, String name) {
            this.managerId = managerId;
            this.name = name;
        }

        public Integer getManagerId() {
            return managerId;
        }

        public void setManagerId(Integer managerId) {
            this.managerId = managerId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}