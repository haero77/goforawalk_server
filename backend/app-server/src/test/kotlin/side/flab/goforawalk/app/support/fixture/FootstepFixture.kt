package side.flab.goforawalk.app.support.fixture

import side.flab.goforawalk.app.domain.footstep.domain.Footstep
import side.flab.goforawalk.app.domain.footstep.domain.FootstepRepository
import side.flab.goforawalk.app.domain.user.domain.User
import java.time.LocalDate

object FootstepFixture {
    private const val DEFAULT_IMAGE_URL = "https://example.com/image.jpg"
    private const val DEFAULT_CONTENT = "오늘의 산책은 정말 좋았어요!"

    fun createFootstep(user: User, date: LocalDate): Footstep {
        return Footstep(
            user = user,
            date = date,
            imageUrl = DEFAULT_IMAGE_URL,
            content = DEFAULT_CONTENT,
        )
    }

    fun Footstep.save(repo: FootstepRepository): Footstep {
        repo.save(this)
        return this
    }

    fun Footstep.deleted(): Footstep {
        this.delete()
        return this
    }
}