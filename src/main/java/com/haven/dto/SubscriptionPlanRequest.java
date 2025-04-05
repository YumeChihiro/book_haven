package com.haven.dto;

import java.util.List;

public class SubscriptionPlanRequest {
    
    private String planName;  // Tên gói
    private Float price;      // Giá gói
    private Integer duration; // Thời gian (số ngày)
    private List<SubscriptionBenefitDTO> benefits; // Danh sách lợi ích

    public SubscriptionPlanRequest() {
    }

    public SubscriptionPlanRequest(String planName, Float price, Integer duration, List<SubscriptionBenefitDTO> benefits) {
        this.planName = planName;
        this.price = price;
        this.duration = duration;
        this.benefits = benefits;
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

    public List<SubscriptionBenefitDTO> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<SubscriptionBenefitDTO> benefits) {
        this.benefits = benefits;
    }
}
