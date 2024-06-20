package de.lausi95.misterx.backend.domain.model.team

import de.lausi95.misterx.backend.domain.model.user.UserId
import java.util.UUID

data class TeamId(val value: UUID) {
  constructor(value: String) : this(UUID.fromString(value))

  companion object {
    fun generate(): TeamId = TeamId(UUID.randomUUID())
  }
}

data class Team(
  var teamId: TeamId,
  var name: String,
  var members: MutableSet<UserId>,
) {

  companion object {
    fun of(teamName: String): Team {
      val teamId = TeamId.generate()
      return Team(teamId, teamName, mutableSetOf())
    }
  }

  fun containsMember(userId: UserId): Boolean {
    return members.contains(userId)
  }

  fun assignMember(userId: UserId) {
    members.add(userId)
  }

  fun removeMember(userId: UserId) {
    members.remove(userId)
  }
}

interface TeamRepository {

  fun existsById(teamId: TeamId): Boolean

  fun existsByTeamName(teamName: String): Boolean

  fun findById(teamId: TeamId): Team

  fun findAll(): List<Team>

  fun save(team: Team)
}
