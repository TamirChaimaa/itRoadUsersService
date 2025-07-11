package com.itRoad.users_service.repositories;
import com.itRoad.users_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email, wrapped in an Optional to handle null results
    Optional<User> findByEmail(String email);

    // Find a user by username, wrapped in an Optional to handle null results
    Optional<User> findByUsername(String username);

    // Retrieve all users having the specified role
    List<User> findByRole(String role);

    // Retrieve all users having the specified status
    List<User> findByStatus(String status); // You can remove this too if not needed

    /**
     * Custom query to filter users dynamically based on optional parameters:
     * - If 'name' is provided, it performs a case-insensitive partial match on the user's name.
     * - If 'role' is provided, it filters by role.
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:role IS NULL OR u.role = :role)")
    List<User> findByFilters(@Param("name") String name,
                             @Param("role") String role);

    // Check if a user exists with the given email
    boolean existsByEmail(String email);

    // Check if a user exists with the given username
    boolean existsByUsername(String username);
}
