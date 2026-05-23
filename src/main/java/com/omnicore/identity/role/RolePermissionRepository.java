package com.omnicore.identity.role;

import com.omnicore.identity.permission.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface RolePermissionRepository extends Repository<Permission, Long> {

    @Query(
        value = "SELECT COUNT(*) FROM role_permissions WHERE permission_id = :permissionId",
        nativeQuery = true
    )
    long countByPermissionId(@Param("permissionId") Long permissionId);
}
