package com.github.jiramot.security

import com.github.jiramot.security.model.UserContext

interface TokenService {
    companion object {
        const val ISSUER = "https://security.irm.im"
        const val CLAIM_SCOPE = "scope"
        const val CLAIM_ID_CARD = "id_card"
        const val CLAIM_PHONE_NUMBER = "phone_number"
        const val CLAIM_PEOPLE_ID = "people_id"
    }

    fun createAccessToken(userContext: UserContext): String

    fun verify(token: String): UserContext
}