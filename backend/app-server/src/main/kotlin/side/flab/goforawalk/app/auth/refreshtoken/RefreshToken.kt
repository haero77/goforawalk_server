package side.flab.goforawalk.app.auth.refreshtoken

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import side.flab.goforawalk.app.support.base.BaseEntityWithoutActiveStatus
import java.time.Instant

@Entity
class RefreshToken(
  @Column(name = "user_id", nullable = false, updatable = false)
  val userId: Long,

  @Column(name= "token", updatable = false, length = 512)
  val token: String,

  @Column(name = "issued_at", nullable = false, updatable = false)
  val issuedAt: Instant,

  @Column(name = "expired_at", nullable = false, updatable = false)
  val expiredAt: Instant
) : BaseEntityWithoutActiveStatus() {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null

  fun expired(now: Instant): Boolean {
    return now.isAfter(this.expiredAt)
  }

  fun tokenEquals(otherToken: String): Boolean {
    return this.token == otherToken
  }
}