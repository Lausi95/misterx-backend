package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserId
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.stereotype.Component

class RemoveUserFromTeamException: Exception {
  constructor(cause: Throwable?) : super("Cannot remove user from team.", cause)
  constructor(message: String?) : super("Cannot remove user from team: $message")
}

data class RemoveUserFromTeamCommand(
  val userId: UserId,
  val teamId: TeamId,
)

@Component
class RemoveUserFromTeamUsecase(private val userRepository: UserRepository, private val teamRepository: TeamRepository) {

  fun removeUserFromTeam(cmd: RemoveUserFromTeamCommand) {
    try {
      tryRemoveUserFromTeam(cmd)
    } catch (ex: RemoveUserFromTeamException) {
      throw ex
    } catch (ex: Exception) {
      throw RemoveUserFromTeamException(ex)
    }
  }

  private fun tryRemoveUserFromTeam(cmd: RemoveUserFromTeamCommand) {
    if (!userRepository.existsById(cmd.userId)) {
      throw RemoveUserFromTeamException("User with id ${cmd.userId} does not exist.")
    }

    val team = teamRepository.findById(cmd.teamId) ?: throw RemoveUserFromTeamException("Team with id ${cmd.teamId} does not exist.")
    if (!team.containsMember(cmd.userId)) {
      throw RemoveUserFromTeamException("User with id ${cmd.userId} is not in the team with id ${cmd.teamId}.")
    }

    val teamWithoutUser = team.copy(members = team.members.minus(cmd.userId))
    teamRepository.save(teamWithoutUser)
  }
}
