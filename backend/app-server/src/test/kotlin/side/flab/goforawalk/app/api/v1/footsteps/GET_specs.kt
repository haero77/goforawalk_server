package side.flab.goforawalk.app.api.v1.footsteps

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
import kotlin.test.Test

@DisplayName("GET /api/v1/footsteps")
class GET_specs : BaseE2ETest() {
    @Test
    fun `인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다`() {
        // Arrange
        // Act
        val response = given().`when`()
            .get("/api/v1/footsteps")

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
        val user = createSeoulUser("산책왕").save(userRepository)
        createFootstep(user, dateOf("2025-05-25")).save(footstepRepository)
        createFootstep(user, dateOf("2025-05-27")).save(footstepRepository)

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .`when`()
            .get("/api/v1/footsteps")

        // Assert
        response.then()
            .statusCode(200)
            .body(
                "data.footsteps.size()", equalTo(2),
                "data.footsteps[0].userId", notNullValue(),
                "data.footsteps[0].userNickname", equalTo("산책왕"),
                "data.footsteps[0].footstepId", notNullValue(),
                "data.footsteps[0].date", equalTo("2025-05-25"),
                "data.footsteps[0].imageUrl", equalTo("https://example.com/image.jpg"),
                "data.footsteps[0].content", equalTo("오늘의 산책은 정말 좋았어요!"),
                "data.footsteps[0].createdAt", notNullValue(),

                "data.footsteps[1].userId", notNullValue(),
                "data.footsteps[1].userNickname", equalTo("산책왕"),
                "data.footsteps[1].footstepId", notNullValue(),
                "data.footsteps[1].date", equalTo("2025-05-27"),
                "data.footsteps[1].imageUrl", equalTo("https://example.com/image.jpg"),
                "data.footsteps[1].content", equalTo("오늘의 산책은 정말 좋았어요!"),
                "data.footsteps[1].createdAt", notNullValue(),
            )
    }
}