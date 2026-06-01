package com.omnicore.identity.user;

import com.omnicore.identity.common.ApiResponse;
import com.omnicore.identity.common.constants.ApiPaths;
import com.omnicore.identity.user.dto.CreateUserRequest;
import com.omnicore.identity.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.USERS_BASE_PATH)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('LIST_USER')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> listUsers(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ){
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully",userService.list(search, pageable)));

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully",userService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User created", userService.create(request)));
    }




}
