package side.flab.goforawalk.app.support.sentry

import io.sentry.SentryOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class SentryConfig {
  @Bean
  fun beforeSendCallback(): SentryOptions.BeforeSendCallback {
    return SentryOptions.BeforeSendCallback { event, _ ->
      /*
       * 모든 에러에 랜덤 UUID를 지문(Fingerprint)으로 설정한다.
       * Sentry는 지문이 다르면 별개의 'Issue'로 판단하므로,
       * 이미 발생했던 에러라도 'New Issue' 알림이 매번 전송됩니다. (Better Stack 사용 시 fingerprint가 달라야 New Alert가 전송됨)
       */
      event.fingerprints = listOf(UUID.randomUUID().toString())

      event
    }
  }
}