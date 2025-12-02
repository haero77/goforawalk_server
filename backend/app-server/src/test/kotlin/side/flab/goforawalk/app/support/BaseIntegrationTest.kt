package side.flab.goforawalk.app.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.jdbc.Sql
import side.flab.goforawalk.app.auth.AppAuthTokenProvider
import side.flab.goforawalk.app.auth.JwtProperties
import side.flab.goforawalk.app.domain.footstep.domain.FootstepRepository
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.domain.user.domain.UserRepository
import side.flab.goforawalk.app.support.fixture.AuthFixture
import side.flab.goforawalk.app.support.fixture.LoginFixture.generateAT

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

  @Autowired
  lateinit var authTokenProvider: AppAuthTokenProvider

  @Autowired
  lateinit var jwtProperties: JwtProperties

  @Autowired
  lateinit var authFixture: AuthFixture

  fun generateAccessToken(user: User): String {
    return generateAT(
      provider = authTokenProvider,
      user = user
    )
  }
}