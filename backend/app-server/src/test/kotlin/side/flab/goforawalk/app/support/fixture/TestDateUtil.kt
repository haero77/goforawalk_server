package side.flab.goforawalk.app.support.fixture

import java.time.LocalDate

object TestDateUtil {
    fun dateOf(dateStr: String): LocalDate {
        return LocalDate.parse(dateStr)
    }
}