package com.omnicore.identity.permission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByName(String name);

    Optional<Permission> findByName(String name);

    List<Permission> findAllByIdInAndActiveTrueAndDeletedAtIsNull(Collection<Long> ids);

    List<Permission> findAllBySystemTrueAndActiveTrueAndDeletedAtIsNull();
}
