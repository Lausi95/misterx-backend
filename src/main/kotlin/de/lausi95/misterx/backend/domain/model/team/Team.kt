package de.lausi95.misterx.backend.domain.model.team

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import java.util.*

data class FoundMisterx(val misterxId: UUID, val time: LocalDateTime)

data class Team(
  @Id var id: UUID,
  var name: String,
  var members: MutableSet<UUID>,
  var foundMisterx: MutableSet<FoundMisterx>
)

interface TeamRepository : MongoRepository<Team, UUID> {

  fun existsByName(teamName: String): Boolean

  fun findByMembersContaining(userId: UUID): Optional<Team>

  fun save(team: Team)
}
