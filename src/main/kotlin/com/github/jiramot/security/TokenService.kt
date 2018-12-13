package com.github.jiramot.security

import com.github.jiramot.security.model.UserContext

interface TokenService {
    companion object {
        const val ISSUER = "https://security.irm.im"
        const val CLAIM_SCOPE = "scope"
        const val CLAIM_PAYLOAD = "payload"
    }

    fun createAccessToken(userContext: UserContext): String
    fun createRefreshToken(userContext: UserContext): String
    fun verify(token: String): UserContext
}