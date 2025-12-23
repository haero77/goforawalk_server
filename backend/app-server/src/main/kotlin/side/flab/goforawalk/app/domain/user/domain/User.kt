package side.flab.goforawalk.app.domain.user.domain

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import side.flab.goforawalk.app.support.base.BaseEntity
import side.flab.goforawalk.app.support.util.ClockHolder
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Entity
@Table(
  name = "users",
  uniqueConstraints = [
    UniqueConstraint(
      name = "uk_provider_provider_username",
      columnNames = ["provider", "provider_username"]
    ),
    UniqueConstraint(
      name = "uk_nickname",
      columnNames = ["nickname"]
    )
  ]
)
@SQLRestriction(BaseEntity.SOFT_DELETE_RESTRICTION)
class User constructor(
  @Column(name = "email", length = 50)
  var email: String? = null,

  @Column(name = "provider", nullable = false, updatable = false, length = 10)
  @Enumerated(EnumType.STRING)
  val provider: OAuth2Provider,

  @Column(name = "provider_username", nullable = false, length = 100)
  var providerUsername: String,

  nickname: String,

  @Column(name = "time_zone", nullable = false, length = 50)
  val timeZone: String = "Asia/Seoul",
) : BaseEntity() {

  @Column(name = "nickname", nullable = false, length = 50)
  var nickname: String = nickname
    set(value) {
      require(value.isNotBlank()) { "nickname must have text" }
      field = value
    }

  @Column(name = "role", nullable = false, length = 10)
  @Enumerated(EnumType.STRING)
  var role: UserRole = UserRole.USER // 유저 role 기본값이 USER이므로 생성자에서 받지 않음.

  init {
    this.nickname = nickname
  }

  companion object {
    const val NICKNAME_UPDATE_MAX_LENGTH = 8

    fun of(
      provider: OAuth2Provider,
      providerUsername: String,
      email: String? = null
    ): User {
      return User(
        provider = provider,
        providerUsername = providerUsername,
        nickname = generateRandomNickname(),
        email = email
      )
    }

    private fun generateRandomNickname(): String {
      val uuid = UUID.randomUUID().toString()
      return "user_${uuid}"
    }
  }

  fun updateNickname(nickname: String) {
    this.nickname = nickname
  }

  fun getLocalDate(clockHolder: ClockHolder): LocalDate {
    return clockHolder.localDate(getTimeZone())
  }

  override fun delete() {
    super.delete()
    this.providerUsername = "deleted_user_${id}"
  }

  private fun getTimeZone(): ZoneId {
    return ZoneId.of(timeZone)
  }

  override fun toString(): String {
    return "User(" +
        "email=$email, " +
        "provider=$provider, " +
        "providerUsername='$providerUsername', " +
        "nickname='$nickname', " +
        "role=$role" +
        ")"
  }
}