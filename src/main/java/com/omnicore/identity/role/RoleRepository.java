package com.omnicore.identity.role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

  boolean existsByName(String name);

  Optional<Role> findByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);

  List<Role> findAllByIdInAndActiveTrueAndDeletedAtIsNull(Collection<Long> ids);

  Optional<Role> findByIdAndDeletedAtIsNull(Long id);

  Optional<Role> findByIdAndActiveTrueAndDeletedAtIsNull(Long id);

  @EntityGraph(attributePaths = "permissions")
  Optional<Role> findWithPermissionsByIdAndDeletedAtIsNull(Long id);

  @EntityGraph(attributePaths = "permissions")
  Optional<Role> findWithPermissionsByName(String name);

  @Query(
      value =
          """
          SELECT COUNT(*)
          FROM user_roles ur
          JOIN users u ON u.id = ur.user_id
          WHERE ur.role_id = :roleId
          AND u.deleted_at IS NULL
          """,
      nativeQuery = true)
  long countAssignedUsersByRoleId(@Param("roleId") Long roleId);
}
