package com.github.jiramot.security.config

interface JwtSecurityUrlPermit {
  fun urls(): Array<String>
}
