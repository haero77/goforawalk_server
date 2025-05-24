package side.flab.goforawalk.app.support.fixture

import side.flab.goforawalk.app.domain.footstep.domain.Footstep
import side.flab.goforawalk.app.domain.footstep.domain.FootstepRepository

object FootstepFixture {
    fun Footstep.save(repo: FootstepRepository) {
        repo.save(this)
    }

    fun Footstep.deleted(): Footstep {
        this.delete()
        return this
    }
}