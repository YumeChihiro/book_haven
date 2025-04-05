package com.haven.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManagerUpdateDTO {
	@NotBlank(message = "Tên không được để trống")
    @Size(max = 50, message = "Tên không được dài quá 50 ký tự")
    private String managerName;

    private Byte[] avatar;

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public Byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(Byte[] avatar) {
		this.avatar = avatar;
	}
    
    
}


