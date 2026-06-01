package com.omnicore.identity.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph(attributePaths = {"roles", "roles.permissions"})
  Optional<User> findByEmailAndDeletedAtIsNull(String email);

  Optional<User> findByEmail(String email);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findWithRolesByEmail(String email);

  @EntityGraph(attributePaths = {"roles", "roles.permissions"})
  Optional<User> findByIdAndDeletedAtIsNull(Long id);

  @EntityGraph(attributePaths = {"roles"})
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findByIdWithRoles(@Param("id") Long id);

  @Query("SELECT u FROM User u WHERE " +
          "(:search IS NULL OR :search = '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
          "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
          "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<User> search(@Param("search") String search, Pageable pageable);

  boolean existsByEmail(String email);
}
