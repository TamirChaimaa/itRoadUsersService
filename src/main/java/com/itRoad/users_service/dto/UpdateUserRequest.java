package com.itRoad.users_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
}