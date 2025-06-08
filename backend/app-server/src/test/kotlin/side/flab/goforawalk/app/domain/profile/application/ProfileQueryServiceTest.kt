package side.flab.goforawalk.app.domain.profile.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.springframework.beans.factory.annotation.Autowired
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.dateOf
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import kotlin.test.Test

class ProfileQueryServiceTest : BaseIntegrationTest() {
    @Autowired
    lateinit var SUT: ProfileQueryService

    @Test
    fun `유저 ID로 프로필을 조회할 수 있다`() {
        // Arrange
        val user = createSeoulUser(
            nickname = "산책왕",
            provider = OAuth2Provider.APPLE
        ).save(userRepository)

        createFootstep(user, dateOf("2025-02-27")).save(footstepRepository)
        createFootstep(user, dateOf("2025-02-28")).save(footstepRepository)
        createFootstep(user, dateOf("2025-03-02")).save(footstepRepository)

        val userId = user.id!!

        // Act
        val profileQueryResponse = SUT.queryProfileByUserId(userId)

        // Assert
        assertAll(
            { assertThat(profileQueryResponse.userId).isEqualTo(userId) },
            { assertThat(profileQueryResponse.userNickname).isEqualTo("산책왕") },
            { assertThat(profileQueryResponse.userProvider).isEqualTo(OAuth2Provider.APPLE) },
            { assertThat(profileQueryResponse.totalFootstepCount).isEqualTo(3) },
            { assertThat(profileQueryResponse.footstepStreakDays).isEqualTo(2) },
        )
    }
}