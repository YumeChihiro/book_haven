package com.haven.dto;

import java.time.LocalDate;

import com.haven.entity.Customer.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerUpdateDTO {
	@NotBlank(message = "Tên không được để trống")
    @Size(max = 50, message = "Tên không được dài quá 50 ký tự")
    private String name;

    @Size(max = 100, message = "Họ tên không được dài quá 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có đúng 10 chữ số")
    private String phone;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate birth;

    private Gender gender;

    private Byte[] avatar;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getBirth() {
		return birth;
	}

	public void setBirth(LocalDate birth) {
		this.birth = birth;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(Byte[] avatar) {
		this.avatar = avatar;
	}
    
    
}
