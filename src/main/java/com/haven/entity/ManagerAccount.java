package com.haven.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "managerAccount")
public class ManagerAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private int accountId;

	@Column(nullable = false)
	private LocalDate createAt;

	private LocalDateTime updateAt;

	private LocalDateTime recentActivity;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id", unique = true)
	private Manager manager;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", unique = true)
	private Customer customer;

	@PrePersist 
	@PreUpdate
	private void validateAccount() {
		if ((manager != null && customer != null) || (manager == null && customer == null)) {
			throw new IllegalStateException(
					"Một tài khoản chỉ có thể là Manager hoặc Customer, không thể cùng lúc cả hai.");
		}
		updateAt = LocalDateTime.now();
		recentActivity = LocalDateTime.now();
	}

	public enum Status {
		Wait, Confirm, Warning, Locked
	}
	
	

	public ManagerAccount() {
		super();
	}

	public ManagerAccount(LocalDate createAt,
			Status status, Manager manager, Customer customer) {
		super();
		this.createAt = createAt;
		this.status = status;
		this.manager = manager;
		this.customer = customer;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public LocalDate getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDate createAt) {
		this.createAt = createAt;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	public LocalDateTime getRecentActivity() {
		return recentActivity;
	}

	public void setRecentActivity(LocalDateTime recentActivity) {
		this.recentActivity = recentActivity;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
}
