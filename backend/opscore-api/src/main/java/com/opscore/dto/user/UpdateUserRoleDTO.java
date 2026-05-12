package com.opscore.dto.user;

import com.opscore.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleDTO {

    @NotNull
    private Role role;
}

