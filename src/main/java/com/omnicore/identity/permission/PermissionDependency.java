package com.omnicore.identity.permission;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "permission_dependencies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDependency {

    @EmbeddedId
    private PermissionDependencyId id;

    @MapsId("permissionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @MapsId("dependencyPermissionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dependency_permission_id", nullable = false)
    private Permission dependencyPermission;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class PermissionDependencyId implements Serializable {

        @Column(name = "permission_id")
        private Long permissionId;

        @Column(name = "dependency_permission_id")
        private Long dependencyPermissionId;
    }
}
