package im.jrm.security

import im.jrm.security.config.JwtConfiguration
import im.jrm.security.model.UserContext
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Service
class TokenService {
  companion object {
    const val ISSUER = "https://security.irm.im"
    const val CLAIM_SCOPE = "scope"
  }

  @Autowired
  lateinit var jwtConfig: JwtConfiguration

  fun createAccessToken(userContext: UserContext): String {
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
        .signWith(signingKey, signatureAlgorithm)
        .compact()
  }

  fun verify(token: String): UserContext {
    val claims = Jwts.parser()
        .setSigningKey(DatatypeConverter.parseBase64Binary(jwtConfig.secret))
        .parseClaimsJws(token).getBody()
    val scope = claims.get(CLAIM_SCOPE) as ArrayList<String>
    return UserContext(claims.subject, scope)
  }
}