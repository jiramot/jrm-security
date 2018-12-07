package com.github.jiramot.security

import com.github.jiramot.security.TokenService.Companion.CLAIM_ID_CARD
import com.github.jiramot.security.TokenService.Companion.CLAIM_PEOPLE_ID
import com.github.jiramot.security.TokenService.Companion.CLAIM_PHONE_NUMBER
import com.github.jiramot.security.TokenService.Companion.CLAIM_SCOPE
import com.github.jiramot.security.TokenService.Companion.ISSUER
import com.github.jiramot.security.config.JwtConfiguration
import com.github.jiramot.security.model.UserContext
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Service
class DefaultTokenService : TokenService {
  @Autowired
  lateinit var jwtConfig: JwtConfiguration

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
        .claim(CLAIM_PHONE_NUMBER, userContext.phoneNumber)
        .claim(CLAIM_ID_CARD, userContext.idCard)
        .signWith(signingKey, signatureAlgorithm)
        .compact()
  }

  override fun verify(token: String): UserContext {
    val claims = Jwts.parser()
        .setSigningKey(DatatypeConverter.parseBase64Binary(jwtConfig.secret))
        .parseClaimsJws(token).getBody()
    val scope: ArrayList<String> = claims.get(CLAIM_SCOPE) as ArrayList<String>
    val idCard: String = claims.get(CLAIM_ID_CARD).toString()
    val phoneNumber = claims.get(CLAIM_PHONE_NUMBER).toString()
    val peopleId = claims.get(CLAIM_PEOPLE_ID).toString()
    return UserContext(claims.subject, idCard, phoneNumber, scope)
  }
}