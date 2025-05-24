package side.flab.goforawalk.app.domain.footstep.domain

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.support.base.BaseEntity
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.LocalDate

@Entity
// todo: partial index를 이용해서 (user_id, date, entity_status = 'ACTIVE')로 유니크 제약조건 생성
//@Table(
//    name = "footstep",
//    uniqueConstraints = [
//        UniqueConstraint(
//            name = "uk_user_id_date",
//            columnNames = ["user_id", "date"]
//        )
//    ]
//)
@SQLRestriction(BaseEntity.SOFT_DELETE_RESTRICTION)
class Footstep constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,

    @Column(name = "date", nullable = false, updatable = false)
    val date: LocalDate,

    @Column(name = "image_url", length = 255, nullable = false, updatable = false)
    val imageUrl: String,

    @Column(name = "content", length = 50)
    var content: String? = null
) : BaseEntity() {
    companion object {
        fun of(
            user: User,
            clockHolder: ClockHolder,
            imageUrl: String,
            content: String? = null
        ): Footstep {
            return Footstep(
                user = user,
                date = user.getLocalDate(clockHolder),
                imageUrl = imageUrl,
                content = content
            )
        }
    }
}