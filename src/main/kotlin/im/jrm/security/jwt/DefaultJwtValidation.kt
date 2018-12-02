package im.jrm.security.jwt

import com.nimbusds.jwt.JWTClaimsSet

class DefaultJwtValidation : JwtValidation {
    override fun validate(claims: JWTClaimsSet): Boolean {
        return true
    }
}