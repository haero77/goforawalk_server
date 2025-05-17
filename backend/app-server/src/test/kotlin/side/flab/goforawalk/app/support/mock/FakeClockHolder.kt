package side.flab.goforawalk.app.support.mock

import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class FakeClockHolder(
    private val localDate: LocalDate
) : ClockHolder {
    override fun now(): Instant {
        return Instant.now()
    }

    override fun localDate(zoneId: ZoneId): LocalDate {
        return localDate
    }
}