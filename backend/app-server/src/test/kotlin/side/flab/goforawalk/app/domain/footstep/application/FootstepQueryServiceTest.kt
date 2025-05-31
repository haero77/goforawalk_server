package side.flab.goforawalk.app.domain.footstep.application

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.springframework.beans.factory.annotation.Autowired
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.deleted
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.dateOf
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import kotlin.test.Test

class FootstepQueryServiceTest : BaseIntegrationTest() {
    @Autowired
    lateinit var SUT: FootstepQueryService

    @Test
    fun `유저의 전체 발자취 조회`() {
        val user = createSeoulUser(nickname = "산책왕").save(userRepository)
        val f1 = createFootstep(user, dateOf("2025-05-25")).save(footstepRepository)
        val f2 = createFootstep(user, dateOf("2025-05-26")).deleted().save(footstepRepository) // 삭제된 발자취
        val f3 = createFootstep(user, dateOf("2025-05-27")).save(footstepRepository)

        // when
        val actual = SUT.findAllFootStepsOfUser(user.id!!)

        // then
        assertThat(actual).hasSize(2)
            .extracting("userId", "userNickname", "footstepId", "date", "imageUrl")
            .containsExactlyInAnyOrder(
                tuple(user.id, "산책왕", f1.id, dateOf("2025-05-25"), f1.imageUrl),
                tuple(user.id, "산책왕", f3.id, dateOf("2025-05-27"), f3.imageUrl)
            )
    }
}