package de.lausi95.misterx.backend.application.usecase

import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.user.UserId

data class RemoveUserFromTeamCommand(
  val teamId: TeamId,
  val userId: UserId,
)

interface RemoveUserFromTeamUsecase {

  fun removeUserFromTeam(command: RemoveUserFromTeamCommand)
}
