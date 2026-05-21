package com.omnicore.identity.permission;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: user:create, role:assign, menu:read
    @Column(nullable = false, unique = true, length = 150)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String resource;

    @Column(nullable = false, length = 50)
    private String action;

    private String description;

    private boolean active;
}