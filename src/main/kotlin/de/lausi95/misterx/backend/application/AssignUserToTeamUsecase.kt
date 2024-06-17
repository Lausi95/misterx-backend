package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.domain.model.user.UserId
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception

data class AssignUserToTeamCommand(
  val userId: UserId,
  val teamId: TeamId,
)

class AssignUserToTeamException : Exception {
  constructor(message: String?) : super("Cannot assign user to team: $message")
  constructor(cause: Throwable?) : super("Cannot assign user to team", cause)
}

@Service
class AssignUserToTeamUsecase(
  private val teamRepository: TeamRepository,
  private val userRepository: UserRepository,
) {

  @Transactional
  fun assignUserToTeam(command: AssignUserToTeamCommand) {
    try {
      tryAssignUserToTeam(command)
    } catch (ex: AssignUserToTeamException) {
      throw ex
    } catch (ex: Exception) {
      throw AssignUserToTeamException(ex)
    }
  }

  fun tryAssignUserToTeam(command: AssignUserToTeamCommand) {
    // user must exist
    if (!userRepository.existsById(command.userId)) {
      throw AssignUserToTeamException("User with userId ${command.userId} does not exist.")
    }

    // check if the player is alredy in the team
    val team = teamRepository.findById(command.teamId)
      ?: throw AssignUserToTeamException("Team with teamId ${command.teamId} does not exist.")
    if (team.containsMember(command.userId)) {
      throw AssignUserToTeamException("User with userId ${command.userId} is already assigned to the team with id ${command.teamId}.")
    }

    // check if the player is not in another team
    val teams = teamRepository.findAll().filter { it.id != command.teamId }
    if (teams.any { it.containsMember(command.userId) }) {
      throw AssignUserToTeamException("User with userId ${command.userId} is already assigned to a different team.")
    }

    val teamWithMember = team.copy(members = team.members.plus(command.userId))
    teamRepository.save(teamWithMember)
  }
}
