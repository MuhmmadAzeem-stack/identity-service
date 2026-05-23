package com.omnicore.identity.permission;

import com.omnicore.identity.role.Role;
import com.omnicore.identity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EffectivePermissionResolver {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public Set<String> resolvePermissionNames(User user) {
        return resolvePermissions(user).stream()
            .map(Permission::getName)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    @Transactional(readOnly = true)
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
            Permission permission = permissionRepository.findById(permissionId).orElse(null);
            if (permission == null) {
                continue;
            }

            for (Permission dependency : permission.getDependencies()) {
                if (!dependency.isActive() || dependency.isDeleted()) {
                    continue;
                }

                if (effectiveIds.add(dependency.getId())) {
                    pending.addLast(dependency.getId());
                }
            }
        }

        return effectiveIds;
    }
}
