package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.stereotype.Component
import java.util.*

@Component
class FootstepImageNameGenerator {
    fun generate(userId: Long): String {
        return "images/footsteps/$userId/${UUID.randomUUID()}"
    }
}