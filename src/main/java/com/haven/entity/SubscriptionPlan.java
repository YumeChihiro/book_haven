package com.haven.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "plan_name", length = 255, nullable = false)
    private String planName;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "duration", nullable = false)
    private Integer duration;
    
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionBenefit> benefits = new ArrayList<>();

    // Phương thức tiện ích để thêm benefit và thiết lập liên kết ngược
    public void addBenefit(SubscriptionBenefit benefit) {
        benefits.add(benefit);
        benefit.setPlan(this);
    }

    // Phương thức tiện ích để xóa benefit
    public void removeBenefit(SubscriptionBenefit benefit) {
        benefits.remove(benefit);
        benefit.setPlan(null);
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
    
    
}
