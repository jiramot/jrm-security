package im.jrm.security.annotation

import im.jrm.security.config.SecurityConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Configuration
@Import(SecurityConfig::class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
annotation class EnableJwtSecurity