package com.pgbezerra.bezerras.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class RoleDTO implements Serializable {

    private static final long serialVersionUID = -7992857678641988383L;

    @NotBlank(message = "Role name not be empty or unull")
    @Pattern(regexp = "^ROLE_[A-Z]+$", message = "Invalid role name")
    private String roleName;

    public RoleDTO() {
    }

    public RoleDTO(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}


