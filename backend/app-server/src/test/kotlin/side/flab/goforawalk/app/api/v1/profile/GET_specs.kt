package side.flab.goforawalk.app.api.v1.profile

import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import side.flab.goforawalk.app.support.BaseE2ETest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.dateOf
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import side.flab.goforawalk.security.oauth2.OAuth2Provider.APPLE
import kotlin.test.Test

@DisplayName("GET /api/v1/profile")
class GET_specs : BaseE2ETest() {
    @Test
    fun `인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다`() {
        // Arrange
        // Act
        val response = given().`when`()
            .get("/api/v1/profile")

        // Assert
        response.then()
            .statusCode(401)
            .body(
                "code", equalTo("A_4100"),
                "message", notNullValue()
            )
    }

    @Test
    fun `올바르게 요청하면 200 OK 상태 코드를 반환한다`() {
        // Arrange
        val user = createSeoulUser(nickname = "산책왕", provider = APPLE).save(userRepository)
        createFootstep(user, dateOf("2025-02-27")).save(footstepRepository)
        createFootstep(user, dateOf("2025-02-28")).save(footstepRepository)
        createFootstep(user, dateOf("2025-03-02")).save(footstepRepository)

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .get("/api/v1/profile")

        // Assert
        response.then()
            .statusCode(200)
            .body(
                "data.userId", equalTo(user.id!!.toInt()),
                "data.userNickname", equalTo("산책왕"),
                "data.userProvider", equalTo(APPLE.name),
                "data.totalFootstepCount", equalTo(3),
                "data.footstepStreakDays", equalTo(2),
            )
    }
}