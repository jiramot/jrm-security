package im.jrm.security.config

interface JwtSecurityUrlPermit {
  fun urls(): Array<String>
}
