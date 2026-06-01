package com.omnicore.identity.role;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

  boolean existsByName(String name);

  Optional<Role> findByName(String name);

  @EntityGraph(attributePaths = "permissions")
  Optional<Role> findWithPermissionsByName(String name);
}
