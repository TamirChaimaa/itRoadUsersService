package com.itRoad.users_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String address;
    private String phoneNumber;
    private String bio;
    private String role;
    private String status;
    private LocalDate lastLogin;
    private String avatar;
}