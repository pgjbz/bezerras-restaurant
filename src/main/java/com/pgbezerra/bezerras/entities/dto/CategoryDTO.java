package com.pgbezerra.bezerras.entities.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class CategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Category name not be empty or null")
    private String name;
	private Boolean isMenu;

    public CategoryDTO() {
    }

    public CategoryDTO(String name, Boolean isMenu) {
        this.name = name;
        this.isMenu = isMenu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsMenu() {
        return isMenu;
    }

    public void setIsMenu(Boolean menu) {
        isMenu = menu;
    }
}
