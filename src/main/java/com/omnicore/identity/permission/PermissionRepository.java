package com.omnicore.identity.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    List<Permission> findAllByActiveTrue();

    List<Permission> findAllByIdInAndActiveTrue(Collection<Long> ids);

    boolean existsByNameAndIdNot(String name, Long id);

    Optional<Permission> findByIdAndActiveTrue(Long id);

    List<Permission> findAllByIdInAndActiveTrueAndDeletedAtIsNull(Collection<Long> ids);

    List<Permission> findAllBySystemTrueAndActiveTrueAndDeletedAtIsNull();
}
