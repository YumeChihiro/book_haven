package com.haven.service;

import com.haven.dto.CartItemDTO;
import com.haven.dto.CartResponseDTO;
import com.haven.entity.Cart;
import com.haven.entity.CartItem;
import com.haven.entity.Customer;
import com.haven.entity.Product;
import com.haven.repository.CartRepository;
import com.haven.repository.CartItemRepository;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ProductRepository;
import com.haven.repository.ShopProductRepository;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;

    @Transactional
    public void addProductToCart(Integer customerId, Integer productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Tìm customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        // Tìm hoặc tạo giỏ hàng cho customer
        Cart cart = cartRepository.findByCustomerCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });

        // Lấy product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Lấy giá hiện tại từ shop_product
        BigDecimal currentPrice = shopProductRepository.findPriceByProductId(productId);
        if (currentPrice == null) {
            throw new IllegalArgumentException("Product not available in shop: " + productId);
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem cartItem = cartItemRepository.findByCartAndProductProductId(cart, productId)
                .orElse(null);
        
        Integer stock = shopProductRepository.findStockByProductId(productId);
        if (stock == null || stock < quantity) {
            throw new IllegalArgumentException("Không đủ hàng cho sản phẩm: " + productId);
        }

        if (cartItem != null) {
            // Nếu sản phẩm đã có, cập nhật quantity và giá
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setAddedPrice(currentPrice);
            cartItemRepository.save(cartItem);
        } else {
            // Nếu chưa có, tạo mới cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setAddedPrice(currentPrice);
            cartItemRepository.save(newItem);
        }
    }
    
 // Xem giỏ hàng
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Integer customerId) {
        Cart cart = cartRepository.findByCustomerCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer ID: " + customerId));

        CartResponseDTO response = new CartResponseDTO();
        response.setCartId(cart.getCartId());
        response.setCustomerId(cart.getCustomer().getCustomerId());
        response.setItems(cart.getCartItems().stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setCartItemId(item.getCartItemId());
            dto.setProductId(item.getProduct().getProductId());
            dto.setProductName(item.getProduct().getName()); 
            dto.setQuantity(item.getQuantity());
            dto.setAddedPrice(item.getAddedPrice());
            dto.setSelected(item.getSelected());
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }
    
 // Tăng số lượng sản phẩm trong giỏ hàng
    @Transactional
    public CartResponseDTO increaseCartItemQuantity(Integer customerId, Integer cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + cartItemId));

        if (!(cartItem.getCart().getCustomer().getCustomerId() == customerId)) {
            throw new IllegalArgumentException("Cart item does not belong to customer ID: " + customerId);
        }

        BigDecimal currentPrice = shopProductRepository.findPriceByProductId(cartItem.getProduct().getProductId());
        if (currentPrice == null) {
            throw new IllegalArgumentException("Product not available in shop");
        }

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setAddedPrice(currentPrice); // Cập nhật giá mới nhất
        cartItemRepository.save(cartItem);

        return getCart(customerId); // Trả về giỏ hàng sau khi cập nhật
    }

    // Giảm số lượng hoặc xóa sản phẩm
    @Transactional
    public CartResponseDTO decreaseOrRemoveCartItem(Integer customerId, Integer cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + cartItemId));

        if (!(cartItem.getCart().getCustomer().getCustomerId() ==  customerId)) {
            throw new IllegalArgumentException("Cart item does not belong to customer ID: " + customerId);
        }

        int newQuantity = cartItem.getQuantity() - quantity;
        if (newQuantity <= 0) {
            // Nếu quantity mới <= 0, xóa cart item
            cartItemRepository.delete(cartItem);
        } else {
            // Nếu còn lại > 0, giảm quantity
            cartItem.setQuantity(newQuantity);
            BigDecimal currentPrice = shopProductRepository.findPriceByProductId(cartItem.getProduct().getProductId());
            if (currentPrice == null) {
                throw new IllegalArgumentException("Product not available in shop");
            }
            cartItem.setAddedPrice(currentPrice);
            cartItemRepository.save(cartItem);
        }

        return getCart(customerId); // Trả về giỏ hàng sau khi cập nhật
    }
}