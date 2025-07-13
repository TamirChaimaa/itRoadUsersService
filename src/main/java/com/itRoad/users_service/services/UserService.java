package com.itRoad.users_service.services;
import com.itRoad.users_service.Exceptions.EmailAlreadyExistsException;
import com.itRoad.users_service.Exceptions.UserNotFoundException;
import com.itRoad.users_service.Exceptions.UsernameAlreadyExistsException;
import com.itRoad.users_service.dto.CreateUserRequest;
import com.itRoad.users_service.models.User;
import com.itRoad.users_service.repositories.UserRepository;
import com.itRoad.users_service.dto.UserDTO;
import com.itRoad.users_service.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return convertToDTO(user);
    }

    public List<UserDTO> getUsersByFilters(String name, String role, String status) {
        String nameFilter = "all".equals(name) ? null : name;
        String roleFilter = "all".equals(role) ? null : role;
        String statusFilter = "all".equals(status) ? null : status;

        return userRepository.findByFilters(nameFilter, roleFilter)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(@Valid CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLastLogin(LocalDate.now());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    // Add this updated method to your UserService.java class
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }

        // Update email if provided and different from current
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() &&
                !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail().trim());
        }

        // Update bio if provided
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }

        // Update address if provided
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }

        // Update phone number if provided and different from current
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty() &&
                !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDTO updateLastLogin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setLastLogin(LocalDate.now());
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setLastLogin(user.getLastLogin());
        dto.setBio(user.getBio());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }
}
