package com.kawkumputer.centreeducatif.controller;

import com.kawkumputer.centreeducatif.domain.Role;
import com.kawkumputer.centreeducatif.domain.User;
import com.kawkumputer.centreeducatif.dto.AuthRequest;
import com.kawkumputer.centreeducatif.dto.AuthResponse;
import com.kawkumputer.centreeducatif.dto.SignUpRequest;
import com.kawkumputer.centreeducatif.security.JwtTokenProvider;
import com.kawkumputer.centreeducatif.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest request) {
        log.debug("Processing signup request for email: {}", request.getEmail());
        
        try {
            // Par défaut, on attribue le rôle USER comme spécifié dans les MEMORIES
            request.setRole(Role.USER);
            
            User savedUser = userService.createUser(request);
            log.info("User registered successfully with id: {}", savedUser.getId());

            // Authentifier automatiquement l'utilisateur après l'inscription
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            log.debug("JWT token generated for user: {}", request.getEmail());

            return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build());
        } catch (Exception e) {
            log.error("Error during signup for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.debug("Processing login request for email: {}", request.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            log.info("User logged in successfully: {}", request.getEmail());
            log.debug("JWT token generated for user: {}", request.getEmail());

            // Récupérer les informations de l'utilisateur
            User user = userService.getUserByEmail(request.getEmail());

            return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
        } catch (Exception e) {
            log.error("Error during login for email: {}", request.getEmail(), e);
            throw e;
        }
    }
}
