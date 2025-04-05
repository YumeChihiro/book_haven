package com.haven.dto;

public class SubscriptionRegistrationRequest {
    private Integer managerId; // ID quản lý
    private Integer planId;    // ID gói
	public Integer getManagerId() {
		return managerId;
	}
	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}
	public Integer getPlanId() {
		return planId;
	}
	public void setPlanId(Integer planId) {
		this.planId = planId;
	}
    
   
}