package side.flab.goforawalk.app.support.util

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Component
class SystemClockHolder : ClockHolder {
    override fun now(): Instant {
        return getInstant()
    }

    override fun localDate(zoneId: ZoneId): LocalDate {
        return getInstant().atZone(zoneId).toLocalDate()
    }

    private fun getInstant(): Instant = Instant.now()
}