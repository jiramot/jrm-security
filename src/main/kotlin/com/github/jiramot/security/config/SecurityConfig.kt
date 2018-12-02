package com.github.jiramot.security.config

import com.github.jiramot.security.jwt.JwtAuthenticationFailureHandler
import com.github.jiramot.security.jwt.JwtAuthenticationProvider
import com.github.jiramot.security.jwt.JwtHeaderTokenExtractor
import com.github.jiramot.security.jwt.JwtTokenAuthenticationProcessingFilter
import com.github.jiramot.security.util.SkipPathRequestMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class SecurityConfig : WebSecurityConfigurerAdapter() {

  private val ROOT_API_URL = "/**"

  @Autowired
  lateinit var authenticationManager: AuthenticationManager

  @Autowired
  lateinit var jwtAuthenticationProvider: JwtAuthenticationProvider

  @Autowired
  lateinit var jwtAuthenticationFailureHandler: JwtAuthenticationFailureHandler

  @Autowired
  lateinit var jwtPermitConfig: JwtPermitConfig
  @Autowired
  lateinit var tokenExtractor: JwtHeaderTokenExtractor

  override fun configure(http: HttpSecurity) {

    http.csrf().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(*jwtPermitConfig.jwtSecurityUrlPermit().urls())
        .permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterBefore(
            jwtAuthenticationFilter(jwtPermitConfig.jwtSecurityUrlPermit().urls(), ROOT_API_URL),
            UsernamePasswordAuthenticationFilter::class.java
        )
  }

  private fun jwtAuthenticationFilter(
      pathsToSkip: Array<String>,
      pattern: String
  ): JwtTokenAuthenticationProcessingFilter {
    val matcher = SkipPathRequestMatcher(pathsToSkip, pattern)
    val filter = JwtTokenAuthenticationProcessingFilter(
        matcher,
        jwtAuthenticationFailureHandler,
        tokenExtractor
    )
    filter.setAuthenticationManager(this.authenticationManager)
    return filter
  }

  @Bean
  @Throws(Exception::class)
  override fun authenticationManagerBean(): AuthenticationManager {
    return super.authenticationManagerBean()
  }

  @Throws(Exception::class)
  override fun configure(auth: AuthenticationManagerBuilder) {
    auth.authenticationProvider(jwtAuthenticationProvider)
  }
}
