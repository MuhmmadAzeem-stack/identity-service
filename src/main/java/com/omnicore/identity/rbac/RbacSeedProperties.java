package com.omnicore.identity.rbac;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.omnicore.identity.common.constants.SeedConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = SeedConstants.RBAC_SEED_PREFIX)
public class RbacSeedProperties {

  private boolean enabled = true;
  private String superAdminEmail;
  private String superAdminFirstName;
  private String superAdminLastName;
  private String superAdminPassword = "";
  private boolean allowDevDefaultPassword = false;
  private String devDefaultPassword = "";
}
