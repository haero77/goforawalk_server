package side.flab.goforawalk.app.support.util

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Component
class SystemClockHolder : ClockHolder {
    companion object {
        val SEOUL_ZONE_ID: ZoneId = ZoneId.of("Asia/Seoul")

        fun toSeoulZonedDateTime(instant: Instant): ZonedDateTime {
            return instant.atZone(SEOUL_ZONE_ID)
        }
    }

    override fun now(): Instant {
        return getInstant()
    }

    override fun localDate(zoneId: ZoneId): LocalDate {
        return getInstant().atZone(zoneId).toLocalDate()
    }

    private fun getInstant(): Instant = Instant.now()
}