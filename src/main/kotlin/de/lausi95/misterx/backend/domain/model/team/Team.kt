package de.lausi95.misterx.backend.domain.model.team

import de.lausi95.misterx.backend.domain.model.user.UserId
import java.util.UUID

data class TeamId(val value: UUID) {
  constructor(value: String): this(UUID.fromString(value))
  companion object {
    fun generate(): TeamId = TeamId(UUID.randomUUID())
  }
}

data class TeamName(val value: String)

data class Team(
  val id: TeamId,
  val name: TeamName,
  val members: List<UserId>,
) {

  fun containsMember(userId: UserId): Boolean {
    return members.contains(userId)
  }
}

interface TeamRepository {

  fun create(team: Team)

  fun save(team: Team)

  fun findById(teamId: TeamId): Team?

  fun findAll(): List<Team>

  fun existsByTeamName(teamName: TeamName): Boolean
}
