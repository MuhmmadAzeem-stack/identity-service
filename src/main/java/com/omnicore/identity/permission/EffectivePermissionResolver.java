package com.omnicore.identity.permission;

import com.omnicore.identity.role.Role;
import com.omnicore.identity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EffectivePermissionResolver {

    private final PermissionRepository permissionRepository;
    private final PermissionDependencyRepository permissionDependencyRepository;

    public Set<String> resolvePermissionNames(User user) {
        return resolvePermissions(user).stream()
            .map(Permission::getName)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    public Set<Permission> resolvePermissions(User user) {
        if (user == null || !user.isActive() || user.isDeleted()) {
            return Set.of();
        }

        Set<Long> directPermissionIds = new LinkedHashSet<>();
        for (Role role : user.getRoles()) {
            if (!role.isActive() || role.isDeleted()) {
                continue;
            }
            for (Permission permission : role.getPermissions()) {
                if (permission.isActive() && !permission.isDeleted()) {
                    directPermissionIds.add(permission.getId());
                }
            }
        }

        if (directPermissionIds.isEmpty()) {
            return Set.of();
        }

        Set<Long> effectiveIds = expandWithDependencies(directPermissionIds);
        return new LinkedHashSet<>(permissionRepository.findAllByIdInAndActiveTrueAndDeletedAtIsNull(effectiveIds));
    }

    private Set<Long> expandWithDependencies(Set<Long> seedPermissionIds) {
        Set<Long> effectiveIds = new LinkedHashSet<>(seedPermissionIds);
        Deque<Long> pending = new ArrayDeque<>(seedPermissionIds);

        while (!pending.isEmpty()) {
            Long permissionId = pending.removeFirst();
            List<PermissionDependency> dependencies =
                permissionDependencyRepository.findByIdPermissionIdIn(List.of(permissionId));

            for (PermissionDependency dependency : dependencies) {
                Permission dependencyPermission = dependency.getDependencyPermission();
                if (dependencyPermission == null
                    || !dependencyPermission.isActive()
                    || dependencyPermission.isDeleted()) {
                    continue;
                }

                Long dependencyId = dependencyPermission.getId();
                if (effectiveIds.add(dependencyId)) {
                    pending.addLast(dependencyId);
                }
            }
        }

        return effectiveIds;
    }
}
