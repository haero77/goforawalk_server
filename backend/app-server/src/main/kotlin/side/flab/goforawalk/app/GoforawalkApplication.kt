package side.flab.goforawalk.app

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class GoforawalkApplication {
  @PostConstruct
  fun init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
  }
}

fun main(args: Array<String>) {
  runApplication<GoforawalkApplication>(*args)
}
