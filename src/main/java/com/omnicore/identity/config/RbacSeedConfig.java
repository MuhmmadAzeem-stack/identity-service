package com.omnicore.identity.config;

import com.omnicore.identity.seed.RbacSeedProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RbacSeedProperties.class)
public class RbacSeedConfig {
}
