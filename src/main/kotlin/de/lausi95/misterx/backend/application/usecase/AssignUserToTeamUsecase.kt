package de.lausi95.misterx.backend.application.usecase

import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.user.UserId

data class AssignUserToTeamCommand(
  val teamId: TeamId,
  val userId: UserId,
)

interface AssignUserToTeamUsecase {

  fun assignUserToTeam(command: AssignUserToTeamCommand)
}
