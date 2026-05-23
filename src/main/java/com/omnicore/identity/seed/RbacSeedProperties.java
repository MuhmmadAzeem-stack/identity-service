package com.omnicore.identity.seed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rbac.seed")
public class RbacSeedProperties {

    private boolean enabled = true;

    private String superAdminEmail = "superadmin@example.com";

    private String superAdminPassword = "";

    private boolean allowDevDefaultPassword = true;

    private String devDefaultPassword = "ChangeMe@123";
}
