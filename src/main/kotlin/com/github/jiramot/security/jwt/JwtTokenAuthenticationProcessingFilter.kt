package com.github.jiramot.security.jwt

import com.github.jiramot.security.model.JwtAuthenticationToken
import com.github.jiramot.security.model.RawAccessJwtToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenAuthenticationProcessingFilter(
    val requestMatcher: RequestMatcher,
    val jwtAuthenticationFailureHandler: JwtAuthenticationFailureHandler,
    val tokenExtractor: JwtHeaderTokenExtractor
) : AbstractAuthenticationProcessingFilter(requestMatcher) {

  val AUTHENTICATION_HEADER_NAME = "Authorization"

  override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
    val tokenPayload = request.getHeader(AUTHENTICATION_HEADER_NAME)
    val token = RawAccessJwtToken(tokenExtractor.extract(tokenPayload))
    return authenticationManager.authenticate(JwtAuthenticationToken(token))
  }

  @Throws(IOException::class, ServletException::class)
  override fun successfulAuthentication(
      request: HttpServletRequest,
      response: HttpServletResponse,
      chain: FilterChain,
      authResult: Authentication
  ) {
    val context = SecurityContextHolder.createEmptyContext()
    context.authentication = authResult
    SecurityContextHolder.setContext(context)
  }

  @Throws(IOException::class, ServletException::class)
  override fun unsuccessfulAuthentication(
      request: HttpServletRequest,
      response: HttpServletResponse,
      failed: AuthenticationException?
  ) {
    SecurityContextHolder.clearContext()
    jwtAuthenticationFailureHandler.onAuthenticationFailure(request, response, failed)
  }
}