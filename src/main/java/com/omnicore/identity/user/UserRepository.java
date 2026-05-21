package com.omnicore.identity.user;

import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {
        "roles",
        "roles.permissions"
    })
    Optional<User> findByUsernameOrEmail(String username, String email);
}