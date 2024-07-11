package de.lausi95.misterx.backend.domain.model

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import java.util.*

data class Location(
  val id: UUID,
  val misterxId: UUID,
  val latitude: Double,
  val longiture: Double,
  val time: LocalDateTime,
)

interface LocationRepository: MongoRepository<Location, UUID> {

  fun findByMisterxIdOrderByTimeDesc(pageable: Pageable, misterxId: UUID): Page<Location>
}
