package com.github.jiramot.security.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtPermitConfig {
  @Bean
  @ConditionalOnMissingBean
  fun jwtSecurityUrlPermit(): JwtSecurityUrlPermit {
    return DefaultJwtSecurityUrlPermit()
  }
}