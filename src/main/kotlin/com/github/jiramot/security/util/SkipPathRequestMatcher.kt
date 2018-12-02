package com.github.jiramot.security.util

import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

class SkipPathRequestMatcher(pathsToSkip: Array<String>, processingPath: String) : RequestMatcher {
    private val matchers: OrRequestMatcher
    private val processingMatcher: RequestMatcher

    init {
        val orMatcher = pathsToSkip.map { AntPathRequestMatcher(it) }.toList()
        matchers = OrRequestMatcher(orMatcher)
        processingMatcher = AntPathRequestMatcher(processingPath)
    }

    override fun matches(request: HttpServletRequest?): Boolean {
        if (matchers.matches(request)) {
            return false
        }
        return processingMatcher.matches(request)
    }
}
