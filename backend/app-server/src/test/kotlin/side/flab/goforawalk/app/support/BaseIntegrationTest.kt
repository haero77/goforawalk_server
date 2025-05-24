package side.flab.goforawalk.app.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.jdbc.Sql
import side.flab.goforawalk.app.domain.footstep.domain.FootstepRepository
import side.flab.goforawalk.app.domain.user.domain.UserRepository

@Profile("test")
@SpringBootTest
@Sql(
    scripts = ["classpath:sql/cleanup.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
abstract class BaseIntegrationTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var footstepRepository: FootstepRepository
}