package com.rbac.service;

import com.rbac.exception.ResourceNotFoundException;
import com.rbac.exception.ResourceAlreadyExistsException;
import com.rbac.exception.InvalidOperationException;

import com.rbac.dto.user.CreateUserRequest;
import com.rbac.dto.user.UpdateUserRequest;
import com.rbac.dto.user.UserResponse;
import com.rbac.entity.Role;
import com.rbac.entity.User;
import com.rbac.repository.RoleRepository;
import com.rbac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public UserResponse createUser(CreateUserRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(request.getIsActive());
        user.setEmailVerified(false);
        user.setEmailVerificationToken(UUID.randomUUID().toString());

        // Assign roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getEmailVerificationToken());

        return new UserResponse(savedUser);
    }

    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            // Check if new username conflicts with existing user (but not the current user)
            if (!user.getUsername().equals(request.getUsername().trim()) && 
                userRepository.existsByUsername(request.getUsername().trim())) {
                throw new ResourceAlreadyExistsException("Username '" + request.getUsername().trim() + "' is already taken");
            }
            user.setUsername(request.getUsername().trim());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Check if new email conflicts with existing user (but not the current user)
            if (!user.getEmail().equals(request.getEmail().trim().toLowerCase()) && 
                userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
                throw new ResourceAlreadyExistsException("Email '" + request.getEmail().trim().toLowerCase() + "' is already registered");
            }
            user.setEmail(request.getEmail().trim().toLowerCase());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        // Update roles
        if (request.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser);
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(user);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::new);
    }

    public Page<UserResponse> getUsersWithFilters(String username, String email, Boolean isActive, Pageable pageable) {
        return userRepository.findWithFilters(username, email, isActive, pageable)
                .map(UserResponse::new);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public UserResponse toggleUserActivation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser);
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, resetToken);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }
}