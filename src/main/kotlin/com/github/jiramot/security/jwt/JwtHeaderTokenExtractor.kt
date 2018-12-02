package com.github.jiramot.security.jwt

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Component

@Component
class JwtHeaderTokenExtractor {
    var HEADER_PREFIX = "Bearer "

    fun extract(header: String): String {
        if (header.isBlank()) {
            throw AuthenticationServiceException("Authorization header cannot be blank!")
        }

        if (header.length < HEADER_PREFIX.length) {
            throw AuthenticationServiceException("Invalid authorization header size.")
        }

        return header.substring(HEADER_PREFIX.length, header.length)
    }
}