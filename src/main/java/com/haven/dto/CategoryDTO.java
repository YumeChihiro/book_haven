package com.haven.dto;


public class CategoryDTO {
	
    private String name;

    private String descriptionCategory;

	public CategoryDTO(String name, String descriptionCategory) {
		super();
		this.name = name;
		this.descriptionCategory = descriptionCategory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescriptionCategory() {
		return descriptionCategory;
	}

	public void setDescriptionCategory(String descriptionCategory) {
		this.descriptionCategory = descriptionCategory;
	}
    
    

  
}