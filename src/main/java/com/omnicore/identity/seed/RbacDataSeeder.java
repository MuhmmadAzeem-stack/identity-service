package com.omnicore.identity.seed;

import com.omnicore.identity.permission.Permission;
import com.omnicore.identity.permission.PermissionDependency;
import com.omnicore.identity.permission.PermissionDependencyRepository;
import com.omnicore.identity.permission.PermissionRepository;
import com.omnicore.identity.role.Role;
import com.omnicore.identity.role.RoleRepository;
import com.omnicore.identity.user.User;
import com.omnicore.identity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.rbac.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RbacDataSeeder implements ApplicationRunner {

    private static final String SUPER_ADMIN_DESCRIPTION = "Full system access";

    private final RbacSeedProperties properties;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionDependencyRepository permissionDependencyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Starting RBAC data seed");

        Map<String, Permission> permissionsByName = seedCorePermissions();
        Role superAdminRole = seedSuperAdminRole();
        assignPermissionsToSuperAdmin(superAdminRole);
        User superAdminUser = seedSuperAdminUser();
        assignSuperAdminRole(superAdminUser, superAdminRole);
        seedPermissionDependencies(permissionsByName);

        log.info("RBAC data seed completed");
    }

    private Map<String, Permission> seedCorePermissions() {
        Map<String, Permission> permissionsByName = new LinkedHashMap<>();

        for (CorePermissionSeed seed : CorePermissionSeeds.all()) {
            Permission permission = permissionRepository.findByName(seed.name())
                .map(existing -> updatePermission(existing, seed))
                .orElseGet(() -> createPermission(seed));

            permissionsByName.put(permission.getName(), permission);
        }

        return permissionsByName;
    }

    private Permission createPermission(CorePermissionSeed seed) {
        Permission permission = Permission.builder()
            .name(seed.name())
            .module(seed.module())
            .action(seed.action())
            .description(seed.description())
            .active(true)
            .system(true)
            .build();

        return permissionRepository.save(permission);
    }

    private Permission updatePermission(Permission permission, CorePermissionSeed seed) {
        boolean changed = false;

        if (!permission.isActive() || permission.isDeleted()) {
            permission.setActive(true);
            permission.setDeletedAt(null);
            permission.setDeletedBy(null);
            changed = true;
        }

        if (!permission.isSystem()) {
            permission.setSystem(true);
            changed = true;
        }

        if (!Objects.equals(permission.getModule(), seed.module())) {
            permission.setModule(seed.module());
            changed = true;
        }

        if (!Objects.equals(permission.getAction(), seed.action())) {
            permission.setAction(seed.action());
            changed = true;
        }

        if (!Objects.equals(permission.getDescription(), seed.description())) {
            permission.setDescription(seed.description());
            changed = true;
        }

        return changed ? permissionRepository.save(permission) : permission;
    }

    private Role seedSuperAdminRole() {
        return roleRepository.findByName(CorePermissionSeeds.SUPER_ADMIN_ROLE)
            .map(this::updateSuperAdminRole)
            .orElseGet(this::createSuperAdminRole);
    }

    private Role createSuperAdminRole() {
        Role role = Role.builder()
            .name(CorePermissionSeeds.SUPER_ADMIN_ROLE)
            .description(SUPER_ADMIN_DESCRIPTION)
            .active(true)
            .system(true)
            .build();

        return roleRepository.save(role);
    }

    private Role updateSuperAdminRole(Role role) {
        boolean changed = false;

        if (!role.isActive() || role.isDeleted()) {
            role.setActive(true);
            role.setDeletedAt(null);
            role.setDeletedBy(null);
            changed = true;
        }

        if (!role.isSystem()) {
            role.setSystem(true);
            changed = true;
        }

        if (!SUPER_ADMIN_DESCRIPTION.equals(role.getDescription())) {
            role.setDescription(SUPER_ADMIN_DESCRIPTION);
            changed = true;
        }

        return changed ? roleRepository.save(role) : role;
    }

    private void assignPermissionsToSuperAdmin(Role superAdminRole) {
        Role managedRole = roleRepository.findWithPermissionsByName(CorePermissionSeeds.SUPER_ADMIN_ROLE)
            .orElse(superAdminRole);

        List<Permission> systemPermissions = permissionRepository.findAllBySystemTrueAndActiveTrueAndDeletedAtIsNull();
        Set<Permission> assignedPermissions = managedRole.getPermissions();
        boolean changed = false;

        for (Permission permission : systemPermissions) {
            boolean alreadyAssigned = assignedPermissions.stream()
                .anyMatch(existing -> Objects.equals(existing.getId(), permission.getId()));

            if (!alreadyAssigned) {
                assignedPermissions.add(permission);
                changed = true;
            }
        }

        if (changed) {
            roleRepository.save(managedRole);
        }
    }

    private User seedSuperAdminUser() {
        String email = properties.getSuperAdminEmail().trim().toLowerCase(Locale.ROOT);

        return userRepository.findByEmail(email)
            .map(this::updateSuperAdminUser)
            .orElseGet(() -> createSuperAdminUser(email));
    }

    private User createSuperAdminUser(String email) {
        User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(resolveInitialPassword()))
            .firstName("Super")
            .lastName("Admin")
            .active(true)
            .system(true)
            .tokenVersion(1)
            .build();

        return userRepository.save(user);
    }

    private User updateSuperAdminUser(User user) {
        boolean changed = false;

        if (!user.isActive() || user.isDeleted()) {
            user.setActive(true);
            user.setDeletedAt(null);
            user.setDeletedBy(null);
            changed = true;
        }

        if (!user.isSystem()) {
            user.setSystem(true);
            changed = true;
        }

        return changed ? userRepository.save(user) : user;
    }

    private void assignSuperAdminRole(User user, Role superAdminRole) {
        User managedUser = userRepository.findWithRolesByEmail(user.getEmail())
            .orElse(user);

        boolean alreadyAssigned = managedUser.getRoles().stream()
            .anyMatch(role -> Objects.equals(role.getId(), superAdminRole.getId()));

        if (!alreadyAssigned) {
            managedUser.getRoles().add(superAdminRole);
            userRepository.save(managedUser);
        }
    }

    private void seedPermissionDependencies(Map<String, Permission> permissionsByName) {
        Map<String, Set<String>> dependencyGraph = loadDependencyGraph();

        for (PermissionDependencySeed seed : CorePermissionDependencySeeds.all()) {
            if (seed.permission().equals(seed.dependsOn())) {
                throw new IllegalStateException(
                    "Permission dependency cannot reference itself: " + seed.permission());
            }

            Permission permission = requirePermission(permissionsByName, seed.permission());
            Permission dependency = requirePermission(permissionsByName, seed.dependsOn());

            if (wouldCreateCycle(dependencyGraph, seed.permission(), seed.dependsOn())) {
                throw new IllegalStateException(
                    "Circular permission dependency detected: "
                        + seed.permission() + " -> " + seed.dependsOn());
            }

            PermissionDependency.PermissionDependencyId id = new PermissionDependency.PermissionDependencyId(
                permission.getId(),
                dependency.getId()
            );

            if (permissionDependencyRepository.existsById(id)) {
                dependencyGraph.computeIfAbsent(seed.permission(), ignored -> new LinkedHashSet<>())
                    .add(seed.dependsOn());
                continue;
            }

            PermissionDependency mapping = PermissionDependency.builder()
                .id(id)
                .permission(permission)
                .dependencyPermission(dependency)
                .build();

            permissionDependencyRepository.save(mapping);
            dependencyGraph.computeIfAbsent(seed.permission(), ignored -> new LinkedHashSet<>())
                .add(seed.dependsOn());
        }
    }

    private Permission requirePermission(Map<String, Permission> permissionsByName, String name) {
        Permission permission = permissionsByName.get(name);
        if (permission == null) {
            throw new IllegalStateException("Missing seeded permission required for dependency: " + name);
        }
        return permission;
    }

    private Map<String, Set<String>> loadDependencyGraph() {
        Map<Long, String> namesById = new HashMap<>();
        permissionRepository.findAll().forEach(permission -> namesById.put(permission.getId(), permission.getName()));

        Map<String, Set<String>> graph = new HashMap<>();
        permissionDependencyRepository.findAll().forEach(mapping -> {
            String permissionName = namesById.get(mapping.getId().getPermissionId());
            String dependencyName = namesById.get(mapping.getId().getDependencyPermissionId());
            if (permissionName != null && dependencyName != null) {
                graph.computeIfAbsent(permissionName, ignored -> new LinkedHashSet<>()).add(dependencyName);
            }
        });

        return graph;
    }

    private boolean wouldCreateCycle(Map<String, Set<String>> graph, String permission, String dependsOn) {
        Deque<String> pending = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        pending.add(dependsOn);

        while (!pending.isEmpty()) {
            String current = pending.removeFirst();
            if (permission.equals(current)) {
                return true;
            }
            if (!visited.add(current)) {
                continue;
            }
            for (String next : graph.getOrDefault(current, Set.of())) {
                pending.addLast(next);
            }
        }

        return false;
    }

    private String resolveInitialPassword() {
        if (StringUtils.hasText(properties.getSuperAdminPassword())) {
            return properties.getSuperAdminPassword();
        }

        if (properties.isAllowDevDefaultPassword()) {
            log.warn(
                "INITIAL_SUPER_ADMIN_PASSWORD is not set; using development default password. "
                    + "Set INITIAL_SUPER_ADMIN_PASSWORD for non-development environments.");
            return properties.getDevDefaultPassword();
        }

        throw new IllegalStateException(
            "INITIAL_SUPER_ADMIN_PASSWORD must be set when app.rbac.seed.allow-dev-default-password=false");
    }
}
