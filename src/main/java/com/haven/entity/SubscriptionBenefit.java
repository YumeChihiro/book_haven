package com.haven.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_benefits")
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionBenefit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

	@Column(name = "benefit_type", length = 50, nullable = false)
	private String benefitType;

	@Column(name = "benefit_value", precision = 10, scale = 2, nullable = false)
	private BigDecimal benefitValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SubscriptionPlan getPlan() {
		return plan;
	}

	public void setPlan(SubscriptionPlan plan) {
		this.plan = plan;
	}

	public String getBenefitType() {
		return benefitType;
	}

	public void setBenefitType(String benefitType) {
		this.benefitType = benefitType;
	}

	public BigDecimal getBenefitValue() {
		return benefitValue;
	}

	public void setBenefitValue(BigDecimal benefitValue) {
		this.benefitValue = benefitValue;
	}
	
	
}
