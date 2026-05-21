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

    @PreAuthorize("hasAuthority('permission:read')")
    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("hasAuthority('permission:read')")
    @Transactional(readOnly = true)
    public PermissionResponse findById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        return toResponse(permission);
    }

    @PreAuthorize("hasAuthority('permission:create')")
    public PermissionResponse create(PermissionCreateRequest request) {
        if (permissionRepository.existsByCode(request.code())) {
            throw new RuntimeException("Permission code already exists");
        }

        Permission permission = Permission.builder()
                .code(request.code())
                .name(request.name())
                .resource(request.resource())
                .action(request.action())
                .description(request.description())
                .active(true)
                .build();

        return toResponse(permissionRepository.save(permission));
    }

    @PreAuthorize("hasAuthority('permission:update')")
    public PermissionResponse update(Long id, PermissionUpdateRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        permission.setName(request.name());
        permission.setResource(request.resource());
        permission.setAction(request.action());
        permission.setDescription(request.description());
        permission.setActive(request.active());

        return toResponse(permission);
    }

    private PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getCode(),
                permission.getName(),
                permission.getResource(),
                permission.getAction(),
                permission.getDescription(),
                permission.isActive()
        );
    }
}