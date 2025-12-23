package side.flab.goforawalk.app.support.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface ClockHolder {
  fun now(): Instant

  fun localDate(zoneId: ZoneId): LocalDate
}