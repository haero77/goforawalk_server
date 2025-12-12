package side.flab.goforawalk.app.auth.refreshtoken

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
  fun findByUserId(userId: Long): RefreshToken?

  @Modifying(clearAutomatically = true)
  @Query("delete from RefreshToken r where r.userId = :userId")
  fun deleteByUserId(@Param("userId") userId: Long)
}