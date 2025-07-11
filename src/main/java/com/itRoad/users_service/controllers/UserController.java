package com.itRoad.users_service.controllers;

import com.itRoad.users_service.dto.CreateUserRequest;
import com.itRoad.users_service.dto.UserDTO;
import com.itRoad.users_service.dto.UpdateUserRequest;
import com.itRoad.users_service.models.User;
import com.itRoad.users_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")  // Allow cross-origin requests from any domain (for dev or APIs)
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint to get all users.
     * Access allowed to users with role 'Admin' or 'Adherant'.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Adherant')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint to get a user by ID.
     * Access allowed if user has 'Admin' or 'Adherant' role and:
     * - is an Admin
     * - OR the user is accessing his own profile
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') or (hasAuthority('Adherant') and #id == authentication.principal.id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint to get the current authenticated user's information.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();  // Cast principal to User model
        UserDTO userDTO = userService.getUserById(currentUser.getId());
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Endpoint to search users by filters (name, role, status).
     * Access allowed to 'Admin' or 'Adherant' roles.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Adherant')")
    public ResponseEntity<List<UserDTO>> getUsersByFilters(
            @RequestParam(defaultValue = "all") String name,
            @RequestParam(defaultValue = "all") String role,
            @RequestParam(defaultValue = "all") String status) {

        List<UserDTO> users = userService.getUsersByFilters(name, role, status);
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint to create a new user.
     * Only accessible by users with 'Admin' role.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Endpoint to update a user by ID.
     * Accessible by 'Admin' or the user himself ('Adherant' with matching ID).
     * If user is not admin, prevent role modification.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') or (hasAuthority('Adherant') and #id == authentication.principal.id)")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        // Check if current user is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"));  // Corrected with ROLE_ prefix

        // If user is not admin, disallow role changes
        if (!isAdmin && request.getRole() != null) {
            request.setRole(null);
        }

        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint to delete a user by ID.
     * Only accessible to users with 'Admin' role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to update the last login timestamp for a user.
     * Accessible to 'Admin' or the user himself.
     */
    @PutMapping("/{id}/last-login")
    @PreAuthorize("hasAuthority('Admin') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateLastLogin(@PathVariable Long id) {
        UserDTO updatedUser = userService.updateLastLogin(id);
        return ResponseEntity.ok(updatedUser);
    }
}
