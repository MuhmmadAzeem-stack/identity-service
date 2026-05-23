package com.omnicore.identity.permission;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionDependencyRepository extends Repository<Permission, Long> {

    @Query(
        value = "SELECT COUNT(*) FROM permission_dependencies WHERE permission_id = :permissionId",
        nativeQuery = true
    )
    long countByPermissionId(@Param("permissionId") Long permissionId);

    @Query("""
        SELECT d FROM Permission p
        JOIN p.dependencies d
        WHERE p.id = :permissionId
        AND d.active = true
        AND d.deletedAt IS NULL
        ORDER BY d.name ASC
        """)
    List<Permission> findActiveDependenciesByPermissionId(@Param("permissionId") Long permissionId);

    @Query("""
        SELECT p FROM Permission p
        JOIN p.dependencies d
        WHERE d.id = :dependencyPermissionId
        AND p.active = true
        AND p.deletedAt IS NULL
        ORDER BY p.name ASC
        """)
    List<Permission> findActivePermissionsDependingOn(
        @Param("dependencyPermissionId") Long dependencyPermissionId
    );
}
