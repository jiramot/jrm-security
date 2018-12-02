package com.github.jiramot.security.jwt

import com.nimbusds.jwt.JWTClaimsSet

interface JwtValidation {
    fun validate(claims: JWTClaimsSet): Boolean
}