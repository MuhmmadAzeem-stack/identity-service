package com.omnicore.identity.permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PermissionRepository
    extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

  Optional<Permission> findByName(String name);

  boolean existsByName(String name);

  List<Permission> findAllByActiveTrue();

  List<Permission> findAllByIdInAndActiveTrue(Collection<Long> ids);

  boolean existsByNameAndIdNot(String name, Long id);

  Optional<Permission> findByIdAndActiveTrue(Long id);

  List<Permission> findAllByIdInAndActiveTrueAndDeletedAtIsNull(Collection<Long> ids);

  List<Permission> findAllBySystemTrueAndActiveTrueAndDeletedAtIsNull();

  Optional<Permission> findByIdAndDeletedAtIsNull(Long id);

  @Query(
      """
        SELECT p FROM Permission p
        LEFT JOIN FETCH p.dependencies
        WHERE p.id = :id
        AND p.deletedAt IS NULL
        """)
  Optional<Permission> findByIdWithDependencies(@Param("id") Long id);
}
