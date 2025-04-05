package com.haven.dto;

import com.haven.entity.Address.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddressDTO {

    @Size(max = 200, message = "Tên đường không được dài quá 200 ký tự")
    private String road;

    @NotBlank(message = "Xã/Phường không được để trống")
    @Size(max = 100, message = "Xã/Phường không được dài quá 100 ký tự")
    private String town;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 100, message = "Quận/Huyện không được dài quá 100 ký tự")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100, message = "Tỉnh/Thành phố không được dài quá 100 ký tự")
    private String province;

    @NotNull(message = "Loại địa chỉ không được để trống")
    private AddressType type;

    public AddressDTO() {}

    public AddressDTO(String road, String town, String district, String province, AddressType type) {
        this.road = road;
        this.town = town;
        this.district = district;
        this.province = province;
        this.type = type;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }
}