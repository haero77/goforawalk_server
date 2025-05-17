package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.data.jpa.repository.JpaRepository

interface FootstepRepository : JpaRepository<Footstep, Long> {
}