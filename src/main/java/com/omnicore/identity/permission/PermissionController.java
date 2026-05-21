package com.omnicore.identity.permission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public List<PermissionResponse> findAll() {
        return permissionService.findAll();
    }

    @GetMapping("/{id}")
    public PermissionResponse findById(@PathVariable Long id) {
        return permissionService.findById(id);
    }

    @PostMapping
    public PermissionResponse create(@Valid @RequestBody PermissionCreateRequest request) {
        return permissionService.create(request);
    }

    @PutMapping("/{id}")
    public PermissionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PermissionUpdateRequest request
    ) {
        return permissionService.update(id, request);
    }
}