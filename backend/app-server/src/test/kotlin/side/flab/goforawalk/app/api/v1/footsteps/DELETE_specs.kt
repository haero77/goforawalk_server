package side.flab.goforawalk.app.api.v1.footsteps

import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import side.flab.goforawalk.app.support.BaseE2ETest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.dateOf
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import kotlin.test.Test

@DisplayName("DELETE /api/v1/footsteps")
class DELETE_specs : BaseE2ETest() {

    @Test
    fun `인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다`() {
        // Arrange
        // Act
        val response = given().`when`()
            .delete("/api/v1/footsteps/{footstepId}", 1L)

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
        val footstep = createFootstep(user, dateOf("2025-05-25")).save(footstepRepository)

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .`when`()
            .delete("/api/v1/footsteps/{footstepId}", footstep.id)

        // Assert
        response.then()
            .statusCode(200)
            .body(emptyString())

        val footstepOpt = footstepRepository.findById(footstep.id!!)
        assertThat(footstepOpt).isEmpty
    }
}