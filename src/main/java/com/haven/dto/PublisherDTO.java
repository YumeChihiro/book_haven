package com.haven.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PublisherDTO {

    private int publisherId;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên không được dài quá 100 ký tự")
    private String name;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 500, message = "Mô tả không được dài quá 500 ký tự")
    private String description;

    // Constructors
    public PublisherDTO() {}

    public PublisherDTO(int publisherId, String name, String description) {
        this.publisherId = publisherId;
        this.name = name;
        this.description = description;
    }

    // Getters, Setters
    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}