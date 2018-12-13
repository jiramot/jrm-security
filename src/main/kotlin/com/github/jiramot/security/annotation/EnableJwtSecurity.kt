package com.github.jiramot.security.annotation

import com.github.jiramot.security.config.SecurityConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Configuration
@Import(SecurityConfig::class)
@EnableWebSecurity
annotation class EnableJwtSecurity