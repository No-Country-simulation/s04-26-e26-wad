package com.opscore.dto.user;

import com.opscore.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}

