package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface FootstepRepository : JpaRepository<Footstep, Long> {
    @Query(
        """
        select count(fs.id) > 0
        from Footstep fs 
        where fs.user.id = :userId and fs.date = :date"""
    )
    fun existsByUserIdAndDate(userId: Long, date: LocalDate): Boolean
}