package com.omnicore.identity.permission;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PermissionDependencyRepository extends JpaRepository<PermissionDependency, PermissionDependency.PermissionDependencyId> {

    @EntityGraph(attributePaths = "dependencyPermission")
    List<PermissionDependency> findByIdPermissionIdIn(Collection<Long> permissionIds);
}
