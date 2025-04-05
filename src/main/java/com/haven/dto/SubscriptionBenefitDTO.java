package com.haven.dto;

import java.math.BigDecimal;

public class SubscriptionBenefitDTO {
    private String benefitType; // Ví dụ: "DISCOUNT", "FREE_SHIPPING"
    private BigDecimal benefitValue; // Giá trị lợi ích
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
