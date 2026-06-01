package com.omnicore.identity.role;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface RolePermissionRepository extends Repository<Role, Long> {

  @Query(
      value = "SELECT COUNT(*) FROM role_permissions WHERE permission_id = :permissionId",
      nativeQuery = true)
  long countByPermissionId(@Param("permissionId") Long permissionId);

  @Query(
      value =
          """
            SELECT COUNT(*) FROM role_permissions rp
            INNER JOIN roles r ON r.id = rp.role_id
            WHERE rp.permission_id = :permissionId
            AND r.is_active = true
            AND r.deleted_at IS NULL
            """,
      nativeQuery = true)
  long countActiveRolesByPermissionId(@Param("permissionId") Long permissionId);

  @Query(
      """
        SELECT r FROM Role r
        JOIN r.permissions p
        WHERE p.id = :permissionId
        AND r.active = true
        AND r.deletedAt IS NULL
        ORDER BY r.name ASC
        """)
  List<Role> findActiveRolesByPermissionId(@Param("permissionId") Long permissionId);
}
