package com.omnicore.identity.user;

import com.omnicore.identity.common.BadRequestException;
import com.omnicore.identity.common.DtoMapper;
import com.omnicore.identity.common.ResourceNotFoundException;
import com.omnicore.identity.user.dto.CreateUserRequest;
import com.omnicore.identity.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional(readOnly = true)
    public Page<UserResponse> list(String search, Pageable pageable) {
        return userRepository.search(search, pageable).map(DtoMapper::toUserResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id){
        User user=userRepository.findByIdWithRoles(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
        return DtoMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse create(CreateUserRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new BadRequestException("Email already exists");
        }
        User user=User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .active(request.isActive())
                .roles(request.roleIds())
                .build();
        user=userRepository.save(user);
        return getById(user.getId());
    }

}
