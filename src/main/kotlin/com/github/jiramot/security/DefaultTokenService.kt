package com.github.jiramot.security

import com.github.jiramot.security.TokenService.Companion.CLAIM_PAYLOAD
import com.github.jiramot.security.TokenService.Companion.CLAIM_SCOPE
import com.github.jiramot.security.TokenService.Companion.ISSUER
import com.github.jiramot.security.config.JwtConfiguration
import com.github.jiramot.security.model.UserContext
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Service
class DefaultTokenService(var jwtConfig: JwtConfiguration) : TokenService {

  override fun createAccessToken(userContext: UserContext): String {
    val signatureAlgorithm = SignatureAlgorithm.HS256
    val apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtConfig.secret)
    val signingKey = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)

    val now = Date()
    val expireTime = now.time + jwtConfig.tokenExpirationTime.toMillis()

    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setIssuer(ISSUER)
        .setIssuedAt(now)
        .setSubject(userContext.id)
        .setExpiration(Date(expireTime))
        .claim(CLAIM_SCOPE, userContext.scope)
        .claim(CLAIM_PAYLOAD, userContext.payload)
        .signWith(signingKey, signatureAlgorithm)
        .compact()
  }

  override fun createRefreshToken(userContext: UserContext): String {
    val signatureAlgorithm = SignatureAlgorithm.HS256
    val apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtConfig.secret)
    val signingKey = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)

    val now = Date()
    val expireTime = now.time + jwtConfig.refreshTokenExpTime.toMillis()
    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setIssuer(ISSUER)
        .setIssuedAt(now)
        .setSubject(userContext.id)
        .setExpiration(Date(expireTime))
        .claim(CLAIM_SCOPE, arrayListOf("refresh"))
        .signWith(signingKey, signatureAlgorithm)
        .compact()
  }

  override fun verify(token: String): UserContext {
    val claims = Jwts.parser()
        .setSigningKey(DatatypeConverter.parseBase64Binary(jwtConfig.secret))
        .parseClaimsJws(token).getBody()
    val scope: ArrayList<String> = claims.get(CLAIM_SCOPE) as? ArrayList<String> ?: arrayListOf()
    val payload = claims.get(CLAIM_PAYLOAD) as? HashMap<String, String> ?: hashMapOf<String, String>()
    return UserContext(claims.subject, scope, payload)
  }
}