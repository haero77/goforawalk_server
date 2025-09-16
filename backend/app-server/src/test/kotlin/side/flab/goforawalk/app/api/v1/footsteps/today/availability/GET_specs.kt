package side.flab.goforawalk.app.api.v1.footsteps.today.availability

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.todayDate
import side.flab.goforawalk.app.support.fixture.TestDateUtil.yesterdayDate
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import java.time.LocalDate
import kotlin.test.Test

@DisplayName("GET /api/v1/footsteps/today/availability")
class GET_specs : BaseRestAssuredTest() {

    @Test
    fun `인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다`() {
        // Arrange
        // Act
        val response = given().`when`()
            .get("/api/v1/footsteps/today/availability")

        // Assert
        response.then()
            .statusCode(401)
            .body(
                "code", equalTo("A_4100"),
                "message", notNullValue()
            )
    }

    @Test
    fun `오늘 발자취가 없는 경우 생성 가능하다고 반환한다`() {
        // Arrange
        val user = createSeoulUser(
            nickname = "산책왕A",
            providerUsername = "test-user-a"
        ).save(userRepository)
        createFootstep(user, yesterdayDate()).save(footstepRepository) // 어제 발자취만 생성

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .`when`()
            .get("/api/v1/footsteps/today/availability")

        // Assert
        response.then()
            .statusCode(200)
            .body(
                "data.canCreateToday", equalTo(true),
                "data.todayDate", equalTo(todayDate()),
                "data.existingFootstep", nullValue()
            )
    }

    @Test
    fun `오늘 발자취가 있는 경우 생성 불가하고 기존 발자취 정보를 반환한다`() {
        // Arrange
        val user = createSeoulUser(
            nickname = "산책왕B",
            providerUsername = "test-user-b"
        ).save(userRepository)
        val todayFootstep = createFootstep(user, LocalDate.now()).save(footstepRepository)

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .`when`()
            .get("/api/v1/footsteps/today/availability")

        // Assert
        response.then()
            .statusCode(200)
            .body(
                "data.canCreateToday", equalTo(false),
                "data.todayDate", equalTo(todayDate()),
                "data.existingFootstep.footstepId", equalTo(todayFootstep.id!!.toInt()),
                "data.existingFootstep.imageUrl", equalTo("https://example.com/image.jpg"),
                "data.existingFootstep.content", equalTo("오늘의 산책은 정말 좋았어요!"),
                "data.existingFootstep.createdAt", notNullValue()
            )
    }

    @Test
    fun `다른 사용자의 오늘 발자취가 있어도 본인은 생성 가능하다`() {
        // Arrange
        val user1 = createSeoulUser(
            nickname = "사용자1",
            providerUsername = "test-user-1"
        ).save(userRepository)

        val user2 = createSeoulUser(
            nickname = "사용자2",
            providerUsername = "test-user-2"
        ).save(userRepository)

        createFootstep(user2, LocalDate.now()).save(footstepRepository) // user2의 오늘 발자취

        // Act
        val response = given()
            .header("Authorization", "Bearer ${generateAccessToken(user1)}")
            .`when`()
            .get("/api/v1/footsteps/today/availability")

        // Assert
        response.then()
            .statusCode(200)
            .body(
                "data.canCreateToday", equalTo(true),
                "data.todayDate", equalTo(todayDate()),
                "data.existingFootstep", nullValue()
            )
    }
}