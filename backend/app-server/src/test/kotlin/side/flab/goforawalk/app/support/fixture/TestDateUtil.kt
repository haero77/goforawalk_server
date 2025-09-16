package side.flab.goforawalk.app.support.fixture

import java.time.LocalDate

object TestDateUtil {
    fun dateOf(dateStr: String): LocalDate {
        return LocalDate.parse(dateStr)
    }
    
    fun todayDate(): String {
        return LocalDate.now().toString()
    }
    
    fun yesterdayDate(): LocalDate {
        return LocalDate.now().minusDays(1)
    }
}