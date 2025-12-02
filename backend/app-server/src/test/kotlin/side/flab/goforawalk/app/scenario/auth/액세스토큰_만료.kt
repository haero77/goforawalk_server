package side.flab.goforawalk.app.scenario.auth

import org.hamcrest.Matchers.equalTo
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.error.ApiErrorCode
import side.flab.goforawalk.app.support.fixture.UserFixture
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import kotlin.test.Test

class 액세스토큰_만료 : BaseRestAssuredTest() {

  @Test
  fun `만료된 액세스 토큰으로 인증 시도 시 401 에러, code=A4101 응답`() {
    val user = UserFixture.createSeoulUser().save(userRepository)
    val expiredAccessToken = authFixture.generateExpiredAccessToken(user)

    authFixture.givenAuthorizationBearerHeader(expiredAccessToken)
      .log().all()
      .`when`()
      .post("/api/v1/profile")
      .then()
      .log().all()
      .statusCode(401)
      .body("code", equalTo(ApiErrorCode.A_4101.name))
  }
}
