package com.example.demo.dto;

import com.example.demo.enums.Provider;
import com.example.demo.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {
    private Long id;
    private int position;
    private String username;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
    private Integer wins;
    private Integer loses;
    private Long vkId;
    private Provider provider;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
