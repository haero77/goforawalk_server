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

  @Query(
    """
        select fs 
        from Footstep fs 
            join fetch fs.user u
        where fs.id = :footstepId"""
  )
  fun findByIdFetchJoinUser(footstepId: Long): Footstep?

  @Query(
    """
        select fs 
        from Footstep fs
            join fetch fs.user u
        where fs.user.id = :userId"""
  )
  fun findAllByUserIdFetchJoinUser(userId: Long): List<Footstep>

  @Query(
    """
        select fs 
        from Footstep fs
            join fetch fs.user u
        where fs.user.id = :userId and fs.date between :startDate and :endDate"""
  )
  fun findAllByUserIdAndDateBetween(userId: Long, startDate: LocalDate, endDate: LocalDate): List<Footstep>

  @Query(
    """
        select fs 
        from Footstep fs
            join fetch fs.user u
        where fs.user.id = :userId and fs.date = :date"""
  )
  fun findByUserIdAndDate(userId: Long, date: LocalDate): Footstep?
}