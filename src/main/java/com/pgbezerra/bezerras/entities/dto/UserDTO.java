package com.pgbezerra.bezerras.entities.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;

public class UserDTO  implements Serializable  {

    private static final long serialVersionUID = 3045788185662714545L;

    @NotBlank(message = "Name of user not be empty or null")
    private String name;
    @NotBlank(message = "Username not be empty or null")
    private String username;
    @NotBlank(message = "Password not be empty or null")
    private String password;
    @PositiveOrZero(message = "Role id not less than 0")
    private Integer role;

    public UserDTO(String name, String username, String password, Integer role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
