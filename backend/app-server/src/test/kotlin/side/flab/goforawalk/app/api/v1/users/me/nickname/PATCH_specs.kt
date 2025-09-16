package side.flab.goforawalk.app.api.v1.users.me.nickname

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import side.flab.goforawalk.app.domain.profile.application.dto.UserNicknameUpdateRequest
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import kotlin.test.Test

@DisplayName("PATCH /api/v1/users/me/nickname/nickname")
class PATCH_specs : BaseRestAssuredTest() {
    @Test
    fun `인증 토큰이 유효하지 않을 경우 401 상태 코드를 반환한다`() {
        // Arrange
        // Act
        val response = given()
            .header(AUTHORIZATION, "Bearer invalid_token")
            .log().all()
            .`when`()
            .patch("/api/v1/users/me/nickname")

        // Assert
        response.then()
            .log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body(
                "code", equalTo("A_4100"),
                "message", notNullValue()
            )
    }

    @Test
    fun `올바르게 요청하면 204 상태 코드를 반환한다`() {
        // Arrange
        val user = createSeoulUser("닉네임_변경_전").save(userRepository)
        val requestBody = UserNicknameUpdateRequest(
            nickname = "새로운닉네임"
        )

        // Act
        val response = authFixture.givenAuthenticatedUser(user)
            .contentType(APPLICATION_JSON_VALUE)
            .body(requestBody)
            .log().all()
            .`when`()
            .patch("/api/v1/users/me/nickname")

        // Assert
        response.then()
            .log().all()
            .statusCode(HttpStatus.NO_CONTENT.value())

        val userActual = userRepository.findById(user.id!!).get()
        assertAll(
            { assertThat(userActual.nickname).isEqualTo("새로운닉네임") },
        )
    }

    @Test
    fun `수정 대상이 없으면 400을 반환한다`() {
        // Arrange
        val user = createSeoulUser("닉네임_변경_전").save(userRepository)

        // Act
        val response = authFixture.givenAuthenticatedUser(user)
            .contentType(APPLICATION_JSON_VALUE)
            .log().all()
            .`when`()
            .patch("/api/v1/users/me/nickname")

        // Assert
        response.then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @ParameterizedTest(name = "잘못된 값: {0}")
    @ValueSource(strings = ["", "123456789", "1234567890"])
    fun `잘못된 값을 입력한 경우 400를 반환한다`(nickname: String) {
        // Arrange
        val user = createSeoulUser("닉네임_변경_전").save(userRepository)
        val requestBody = UserNicknameUpdateRequest(
            nickname = nickname
        )

        // Act
        val response = authFixture.givenAuthenticatedUser(user)
            .contentType(APPLICATION_JSON_VALUE)
            .body(requestBody)
            .log().all()
            .`when`()
            .patch("/api/v1/users/me/nickname")

        // Assert
        response.then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }
}