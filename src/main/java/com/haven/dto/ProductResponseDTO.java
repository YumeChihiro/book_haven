package com.haven.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private int productId;
    private String name;
    private String description;
    private Byte[] picture;
    private float weight;
    private AuthorDTO author;
    private CategoryDTO category;
    private PublisherDTO publisher;
    private List<ShopProductDTO> shopProducts;

    @NoArgsConstructor
    public static class AuthorDTO {
        private int authorId;
        private String name;
        
		public AuthorDTO(int authorId, String name) {
			super();
			this.authorId = authorId;
			this.name = name;
		}
		public int getAuthorId() {
			return authorId;
		}
		public void setAuthorId(int authorId) {
			this.authorId = authorId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
        
        
    }

    @NoArgsConstructor
    public static class CategoryDTO {
        private int categoryId;
        private String name;
        
		public CategoryDTO(int categoryId, String name) {
			super();
			this.categoryId = categoryId;
			this.name = name;
		}
		public int getCategoryId() {
			return categoryId;
		}
		public void setCategoryId(int categoryId) {
			this.categoryId = categoryId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
        
        
    }

    
    @NoArgsConstructor
    public static class PublisherDTO {
        private int publisherId;
        private String name;
        
		public PublisherDTO(int publisherId, String name) {
			super();
			this.publisherId = publisherId;
			this.name = name;
		}
		public int getPublisherId() {
			return publisherId;
		}
		public void setPublisherId(int publisherId) {
			this.publisherId = publisherId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
        
        
    }


    @NoArgsConstructor
    public static class ShopProductDTO {
        private int id;
        private BigDecimal price;
        private int quantity;
        
		public ShopProductDTO(int id, BigDecimal price, int quantity) {
			super();
			this.id = id;
			this.price = price;
			this.quantity = quantity;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
        
        
    }


	public int getProductId() {
		return productId;
	}


	public void setProductId(int productId) {
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


	public Byte[] getPicture() {
		return picture;
	}


	public void setPicture(Byte[] picture) {
		this.picture = picture;
	}


	public float getWeight() {
		return weight;
	}


	public void setWeight(float weight) {
		this.weight = weight;
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


	public PublisherDTO getPublisher() {
		return publisher;
	}


	public void setPublisher(PublisherDTO publisher) {
		this.publisher = publisher;
	}


	public List<ShopProductDTO> getShopProducts() {
		return shopProducts;
	}


	public void setShopProducts(List<ShopProductDTO> shopProducts) {
		this.shopProducts = shopProducts;
	}
    
    
}