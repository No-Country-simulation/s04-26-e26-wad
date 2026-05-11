package com.opscore.dto.user;

import com.opscore.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {

    private Long id;

    private String username;

    private Role role;
}

