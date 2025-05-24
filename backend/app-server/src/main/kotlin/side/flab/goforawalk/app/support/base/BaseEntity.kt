package side.flab.goforawalk.app.support.base

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import side.flab.goforawalk.app.support.util.SystemClockHolder
import java.time.Instant
import java.time.ZonedDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_status", nullable = false, length = 10)
    private var entityStatus: EntityStatus = EntityStatus.ACTIVE

    fun active() {
        entityStatus = EntityStatus.ACTIVE
    }

    fun delete() {
        entityStatus = EntityStatus.DELETED
    }

    fun isActive(): Boolean {
        return entityStatus == EntityStatus.ACTIVE
    }

    fun isDeleted(): Boolean {
        return entityStatus == EntityStatus.DELETED
    }

    fun createdAtAsZonedDateTime(): ZonedDateTime {
        return SystemClockHolder.toSystemZonedDateTime(createdAt!!)
    }

    companion object {
        const val SOFT_DELETE_RESTRICTION = "entity_status <> 'DELETED'"
    }
}