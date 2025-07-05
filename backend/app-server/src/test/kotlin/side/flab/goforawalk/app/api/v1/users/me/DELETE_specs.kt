package side.flab.goforawalk.app.api.v1.users.me

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.emptyString
import org.junit.jupiter.api.DisplayName
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import kotlin.test.Test

@DisplayName("DELETE /api/v1/users/me")
class DELETE_specs : BaseRestAssuredTest() {
    @Test
    fun `올바르게 요청하면 200 OK 상태 코드를 반환한다`() {
        // Arrange
        val user = createSeoulUser(
            nickname = "산책왕",
            provider = OAuth2Provider.APPLE,
            email = "test@test.com"
        ).save(userRepository)

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .delete("/api/v1/users/me")

        // Assert
        response.then()
            .statusCode(200)
            .body(emptyString())

        // 회원 삭제 검증
        val userOpt = userRepository.findById(user.id!!)
        assertThat(userOpt).isEmpty()
    }
}