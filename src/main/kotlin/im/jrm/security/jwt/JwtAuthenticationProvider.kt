package im.jrm.security.jwt

import im.jrm.security.TokenService
import im.jrm.security.model.JwtAuthenticationToken
import im.jrm.security.model.RawAccessJwtToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(var tokenService: TokenService) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val rawAccessToken = authentication.credentials as RawAccessJwtToken
        val userContext = tokenService.verify(rawAccessToken.token)
        val authorities: Collection<GrantedAuthority> = userContext.scope.map { SimpleGrantedAuthority(it) }.toList()
        return JwtAuthenticationToken(userContext, authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}