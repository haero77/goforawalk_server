package side.flab.goforawalk.app.support.base

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntityWithoutActiveStatus {
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: Instant? = null

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  var updatedAt: Instant? = null
}