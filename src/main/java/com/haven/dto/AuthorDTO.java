package com.haven.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthorDTO {

    private int authorId;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên không được dài quá 100 ký tự")
    private String name;

    private LocalDate birth;

    @NotBlank(message = "Quốc tịch không được để trống")
    @Size(max = 50, message = "Quốc tịch không được dài quá 50 ký tự")
    private String nationality;

    @Size(max = 500, message = "Mô tả không được dài quá 500 ký tự")
    private String description;

    public AuthorDTO() {}

    public AuthorDTO(int authorId, String name, LocalDate birth, String nationality, String description) {
        this.authorId = authorId;
        this.name = name;
        this.birth = birth;
        this.nationality = nationality;
        this.description = description;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}