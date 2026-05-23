package com.omnicore.identity.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @PreAuthorize("hasAuthority('LIST_PERMISSION')")
    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
            .filter(permission -> !permission.isDeleted())
            .map(this::toResponse)
            .toList();
    }

    @PreAuthorize("hasAuthority('VIEW_PERMISSION')")
    @Transactional(readOnly = true)
    public PermissionResponse findById(Long id) {
        Permission permission = permissionRepository.findById(id)
            .filter(p -> !p.isDeleted())
            .orElseThrow(() -> new RuntimeException("Permission not found"));

        return toResponse(permission);
    }

    @PreAuthorize("hasAuthority('CREATE_PERMISSION')")
    public PermissionResponse create(PermissionCreateRequest request) {
        if (permissionRepository.existsByName(request.name())) {
            throw new RuntimeException("Permission name already exists");
        }

        Permission permission = Permission.builder()
            .name(request.name())
            .module(request.module())
            .action(request.action())
            .description(request.description())
            .active(true)
            .system(false)
            .build();

        return toResponse(permissionRepository.save(permission));
    }

    @PreAuthorize("hasAuthority('UPDATE_PERMISSION')")
    public PermissionResponse update(Long id, PermissionUpdateRequest request) {
        Permission permission = permissionRepository.findById(id)
            .filter(p -> !p.isDeleted())
            .orElseThrow(() -> new RuntimeException("Permission not found"));

        permission.setName(request.name());
        permission.setModule(request.module());
        permission.setAction(request.action());
        permission.setDescription(request.description());
        permission.setActive(request.active());

        return toResponse(permission);
    }

    private PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
            permission.getId(),
            permission.getName(),
            permission.getModule(),
            permission.getAction(),
            permission.getDescription(),
            permission.isActive(),
            permission.isSystem()
        );
    }
}
