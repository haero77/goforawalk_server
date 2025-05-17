package side.flab.goforawalk.app.domain.footstep.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.support.mock.FakeClockHolder
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import java.time.LocalDate
import kotlin.test.Test

class FootstepTest {
    @Test
    fun create_footstep() {
        val sut = Footstep.of(
            user = User(
                provider = OAuth2Provider.KAKAO,
                providerUsername = "provider_username",
                nickname = "nickname",
            ),
            clockHolder = FakeClockHolder(LocalDate.of(2025, 5, 17)),
            imageUrl = "image_url",
        )

        assertAll(
            { assertThat(sut.imageUrl).isEqualTo("image_url") },
            { assertThat(sut.content).isNull() },
            { assertThat(sut.user.nickname).isEqualTo("nickname") },
            { assertThat(sut.user.providerUsername).isEqualTo("provider_username") },
            { assertThat(sut.date).isEqualTo(LocalDate.of(2025, 5, 17)) },
        )
    }
}