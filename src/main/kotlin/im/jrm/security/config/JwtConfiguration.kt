package im.jrm.security.config

import im.jrm.security.jwt.DefaultJwtValidation
import im.jrm.security.jwt.JwtValidation
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtConfiguration(
    var secret: String = "secret",
    @DurationUnit(ChronoUnit.MINUTES)
    var tokenExpirationTime: Duration = Duration.ofMinutes(30),
    @DurationUnit(ChronoUnit.HOURS)
    var refreshTokenExpTime: Duration = Duration.ofHours(24)
) {

  @Bean(name = ["jwtValidation"])
  @ConditionalOnMissingBean
  fun jtiValidation(): JwtValidation {
    return DefaultJwtValidation()
  }
}
