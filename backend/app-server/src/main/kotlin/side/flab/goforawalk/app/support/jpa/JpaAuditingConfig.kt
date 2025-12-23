package side.flab.goforawalk.app.support.jpa

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import side.flab.goforawalk.app.support.util.ClockHolder
import java.util.*

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditDateTimeProvider") // 커스텀 Provider 지정
class JpaAuditingConfig {
  @Bean
  fun auditDateTimeProvider(clockHolder: ClockHolder): DateTimeProvider {
    return DateTimeProvider { Optional.of(clockHolder.now()) }
  }
}