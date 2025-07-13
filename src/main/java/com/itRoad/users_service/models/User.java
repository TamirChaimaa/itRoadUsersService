package com.itRoad.users_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "Adherant";

    private String name;

    @Column(unique = true)
    private String email;

    private String status = "Active";

    private LocalDate lastLogin;

    private String address;

    private String bio;

    @Column(unique = true)
    private String phoneNumber;


}
