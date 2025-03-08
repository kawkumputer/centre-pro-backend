package com.kawkumputer.centreeducatif.dto;

import com.kawkumputer.centreeducatif.domain.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
