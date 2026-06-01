package com.omnicore.identity.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.omnicore.identity.rbac.RbacSeedProperties;

@Configuration
@EnableConfigurationProperties(RbacSeedProperties.class)
public class RbacSeedConfig {}
