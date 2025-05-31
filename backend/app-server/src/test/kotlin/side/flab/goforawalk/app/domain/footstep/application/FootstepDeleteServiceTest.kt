package side.flab.goforawalk.app.domain.footstep.application

import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.dateOf
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import kotlin.test.Test

class FootstepDeleteServiceTest : BaseIntegrationTest() {
    @Autowired
    lateinit var SUT: FootstepDeleteService

    @Test
    fun `footstepId로 발자취 삭제 가능`() {
        val user = createSeoulUser().save(userRepository)
        val footstep = createFootstep(user, dateOf("2025-05-25")).save(footstepRepository)

        // when
        SUT.delete(footstep.id!!)

        // then
        val actual = footstepRepository.findById(footstep.id!!)
        assertThat(actual).isEmpty
    }
}