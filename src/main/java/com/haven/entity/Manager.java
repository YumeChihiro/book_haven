package com.haven.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "managers")
@NoArgsConstructor
@AllArgsConstructor
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_id")
    private int managerId;

    @Column(nullable = false)
    private String managerName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private Byte[] avatar;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore
    private Role role;
    
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Address> addresses;
    
    @OneToOne(mappedBy = "manager", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, 
            orphanRemoval = true, fetch = FetchType.LAZY)
    private ManagerAccount managerAccount;
    
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Shop_Product> shopProducts;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders;
    
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Phía chính được serialize
    private List<ShopSubscription> shopSubscriptions;
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference // Ngăn serialize ngược lại từ sentMessages
    private List<Message> sentMessages;
    
    @OneToMany(mappedBy = "manager", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, 
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoginHistory> loginHistories;

	public int getManagerId() {
		return managerId;
	}

	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}
	
	public List<Message> getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(List<Message> sentMessages) {
		this.sentMessages = sentMessages;
	}

	public List<LoginHistory> getLoginHistories() {
		return loginHistories;
	}

	public void setLoginHistories(List<LoginHistory> loginHistories) {
		this.loginHistories = loginHistories;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(Byte[] avatar) {
		this.avatar = avatar;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public ManagerAccount getManagerAccount() {
		return managerAccount;
	}

	public void setManagerAccount(ManagerAccount managerAccount) {
		this.managerAccount = managerAccount;
	}

	public List<Shop_Product> getShopProducts() {
		return shopProducts;
	}

	public void setShopProducts(List<Shop_Product> shopProducts) {
		this.shopProducts = shopProducts;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<ShopSubscription> getShopSubscriptions() {
		return shopSubscriptions;
	}

	public void setShopSubscriptions(List<ShopSubscription> shopSubscriptions) {
		this.shopSubscriptions = shopSubscriptions;
	}
    
    
    
}
