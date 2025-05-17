package side.flab.goforawalk.app.support.base

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
        get() = requireNotNull(field) { "id must not be null" }

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.MIN

    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.MIN

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_status", nullable = false)
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
}