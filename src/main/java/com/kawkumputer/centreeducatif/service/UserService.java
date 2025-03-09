package com.kawkumputer.centreeducatif.service;

import com.kawkumputer.centreeducatif.domain.User;
import com.kawkumputer.centreeducatif.dto.SignUpRequest;
import com.kawkumputer.centreeducatif.repository.UserRepository;
import com.kawkumputer.centreeducatif.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(SignUpRequest request) {
        log.debug("Creating new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email {} is already registered", request.getEmail());
            throw new RuntimeException("Cet email est déjà enregistré");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        log.debug("Getting current user from security context");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            log.debug("Found UserPrincipal, fetching user with id: {}", userPrincipal.getId());
            return userRepository.findById(userPrincipal.getId());
        }

        log.debug("Principal is not a UserPrincipal: {}", principal.getClass().getName());
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found in provided authentication");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            log.debug("Found UserPrincipal in provided authentication, fetching user with id: {}", userPrincipal.getId());
            return userRepository.findById(userPrincipal.getId());
        }

        log.debug("Principal in provided authentication is not a UserPrincipal: {}", principal.getClass().getName());
        return Optional.empty();
    }
}
