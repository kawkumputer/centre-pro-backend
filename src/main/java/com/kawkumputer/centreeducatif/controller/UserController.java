package com.kawkumputer.centreeducatif.controller;

import com.kawkumputer.centreeducatif.domain.User;
import com.kawkumputer.centreeducatif.dto.UserResponse;
import com.kawkumputer.centreeducatif.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        log.debug("Getting current user details");
        User user = userService.getCurrentUser()
            .orElseThrow(() -> new RuntimeException("Aucun utilisateur connecté"));
        
        return ResponseEntity.ok(mapToUserResponse(user));
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or @userService.getCurrentUser().isPresent() and @userService.getCurrentUser().get().getId() == #id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.debug("Getting user details for id: {}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToUserResponse(user));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }
}
