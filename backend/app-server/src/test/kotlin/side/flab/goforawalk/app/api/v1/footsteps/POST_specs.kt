package side.flab.goforawalk.app.api.v1.footsteps

import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import side.flab.goforawalk.app.support.BaseE2ETest
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import java.time.LocalDate
import kotlin.test.Test

@DisplayName("POST /api/v1/footsteps")
class POST_specs : BaseE2ETest() {

    @Test
    fun `인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다`() {
        // Arrange
        // Act
        val response = given().`when`()
            .post("/api/v1/footsteps")

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
        val imageFile = "test image file".toByteArray()
        val content = "test-content"

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .multiPart("data", "test-image.jpg", imageFile, "image/jpeg")
            .multiPart("content", content, "text/plain")
            .`when`()
            .post("/api/v1/footsteps")

        // Assert
        response.then()
            .statusCode(200)
            .body(
                "data.userId", equalTo(user.id!!.toInt()),
                "data.userNickname", equalTo("산책왕"),
                "data.footstepId", notNullValue(),
                "data.date", equalTo(LocalDate.now().toString()),
                "data.imageUrl", notNullValue(),
                "data.content", equalTo(content),
                "data.createdAt", notNullValue()
            )

        val footstepId = response.jsonPath().getLong("data.footstepId")
        val footstep = footstepRepository.findById(footstepId).get()

        assertAll(
            { assertThat(footstep.content).isEqualTo(content) },
            { assertThat(footstep.imageUrl).isNotBlank() }
        )
    }
}