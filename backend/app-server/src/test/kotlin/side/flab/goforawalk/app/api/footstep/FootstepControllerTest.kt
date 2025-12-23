package side.flab.goforawalk.app.api.footstep

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.hamcrest.CoreMatchers.*
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import java.time.LocalDate
import kotlin.test.Test

class FootstepControllerTest : BaseRestAssuredTest() {
  @Test
  fun `발자취 캘린더 조회 - 성공`() {
    // Arrange
    val user = createSeoulUser("산책왕").save(userRepository)
    val accessToken = generateAccessToken(user)

    // 날짜 범위 내의 발자취들 생성
    val startDate = LocalDate.of(2025, 5, 1)
    val endDate = LocalDate.of(2025, 5, 31)

    val footstep1 = createFootstep(user, LocalDate.of(2025, 5, 5)).save(footstepRepository)
    val footstep2 = createFootstep(user, LocalDate.of(2025, 5, 15)).save(footstepRepository)
    val footstep3 = createFootstep(user, LocalDate.of(2025, 5, 25)).save(footstepRepository)

    // 날짜 범위 밖의 발자취 (결과에 포함되지 않아야 함)
    createFootstep(user, LocalDate.of(2025, 4, 30)).save(footstepRepository)
    createFootstep(user, LocalDate.of(2025, 6, 1)).save(footstepRepository)

    // Act
    val response = given()
      .log().all()
      .header("Authorization", "Bearer $accessToken")
      .queryParam("startDate", startDate.toString())
      .queryParam("endDate", endDate.toString())
      .`when`()
      .get("/api/v1/footsteps/calendar")

    // Assert
    response
      .then()
      .log().all()
      .statusCode(200)
      .body("data", notNullValue())
      .body("data.footsteps.size()", equalTo(3))
      // 첫 번째 발자취 (2025-05-05)
      .body("data.footsteps[0].userId", equalTo(user.id!!.toInt()))
      .body("data.footsteps[0].userNickname", equalTo("산책왕"))
      .body("data.footsteps[0].footstepId", equalTo(footstep1.id!!.toInt()))
      .body("data.footsteps[0].date", equalTo("2025-05-05"))
      .body("data.footsteps[0].imageUrl", notNullValue())
      .body("data.footsteps[0].content", notNullValue())
      .body("data.footsteps[0].createdAt", notNullValue())
      // 두 번째 발자취 (2025-05-15)
      .body("data.footsteps[1].userId", equalTo(user.id!!.toInt()))
      .body("data.footsteps[1].footstepId", equalTo(footstep2.id!!.toInt()))
      .body("data.footsteps[1].date", equalTo("2025-05-15"))
      // 세 번째 발자취 (2025-05-25)
      .body("data.footsteps[2].userId", equalTo(user.id!!.toInt()))
      .body("data.footsteps[2].footstepId", equalTo(footstep3.id!!.toInt()))
      .body("data.footsteps[2].date", equalTo("2025-05-25"))
  }

  @Test
  fun `발자취 캘린더 조회 - 빈 결과`() {
    // Arrange
    val user = createSeoulUser("산책러버").save(userRepository)
    val accessToken = generateAccessToken(user)

    val startDate = LocalDate.of(2025, 5, 1)
    val endDate = LocalDate.of(2025, 5, 31)

    // 날짜 범위 밖의 발자취만 생성
    createFootstep(user, LocalDate.of(2025, 4, 30)).save(footstepRepository)
    createFootstep(user, LocalDate.of(2025, 6, 1)).save(footstepRepository)

    // Act & Assert
    val response = given()
      .log().all()
      .header("Authorization", "Bearer $accessToken")
      .queryParam("startDate", startDate.toString())
      .queryParam("endDate", endDate.toString())
      .`when`()
      .get("/api/v1/footsteps/calendar")

    response
      .then()
      .log().all()
      .statusCode(200)
      .body("data", notNullValue())
      .body("data.footsteps.size()", equalTo(0))
  }

  @Test
  fun `발자취 캘린더 조회 - 인증 실패`() {
    // Arrange
    val startDate = LocalDate.of(2025, 5, 1)
    val endDate = LocalDate.of(2025, 5, 31)

    // Act & Assert
    val response = given()
      .log().all()
      .queryParam("startDate", startDate.toString())
      .queryParam("endDate", endDate.toString())
      .`when`()
      .get("/api/v1/footsteps/calendar")

    response
      .then()
      .log().all()
      .statusCode(401)
      .body("code", notNullValue())
      .body("message", notNullValue())
  }
}