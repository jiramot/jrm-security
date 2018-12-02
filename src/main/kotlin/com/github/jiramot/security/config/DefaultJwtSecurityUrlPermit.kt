package com.github.jiramot.security.config

class DefaultJwtSecurityUrlPermit : JwtSecurityUrlPermit {
  override fun urls() = arrayOf(
      "/v2/api-docs", "/configuration/ui", "/swagger-resources",
      "/configuration/security", "/swagger-ui.html", "/webjars/**",
      "/swagger-resources/configuration/ui", "/swagger-ui.html",
      "/swagger-resources/configuration/security"
  )
}