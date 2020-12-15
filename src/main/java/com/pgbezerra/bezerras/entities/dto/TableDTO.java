package com.pgbezerra.bezerras.entities.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class TableDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "Table name not be empty or null")
    private String name;

    public TableDTO() {
    }

    public TableDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
