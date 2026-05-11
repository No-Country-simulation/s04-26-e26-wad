package com.opscore.service;

import com.opscore.dto.user.*;
import com.opscore.entity.User;
import com.opscore.exception.BadRequestException;
import com.opscore.exception.ResourceNotFoundException;
import com.opscore.repository.UserRepository;
import com.opscore.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(CreateUserRequestDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .build();
    }

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build()
                )
                .toList();
    }

    public UserResponseDTO updateUserRole(
            Long userId,
            UpdateUserRoleDTO request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole())
                .build();
    }

    public UserResponseDTO updateUserStatus(
            Long userId,
            UpdateUserStatusDTO request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        user.setEnabled(request.isEnabled());

        User updatedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole())
                .build();
    }

    public void changePassword(ChangePasswordDTO request) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }


}
