package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.domain.model.team.Team
import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.team.TeamName
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception

data class CreateTeamCommand(
  val teamName: TeamName,
)

class CreateTeamException : Exception {
  constructor(message: String?) : super("Cannot create team: $message")
  constructor(cause: Throwable?) : super("Cannot create team.", cause)
}

@Service
class CreateTeamUsecase(private val teamReposity: TeamRepository) {

  @Transactional
  fun createTeam(command: CreateTeamCommand) {
    try {
      tryCreateTeam(command)
    } catch (ex: CreateTeamException) {
      throw ex
    } catch (ex: Exception) {
      throw CreateTeamException(ex)
    }
  }

  private fun tryCreateTeam(command: CreateTeamCommand) {
    if (teamReposity.existsByTeamName(command.teamName)) {
      throw CreateTeamException("Team with name '${command.teamName}' already exists.")
    }

    val teamId = TeamId.generate()
    val team = Team(teamId, command.teamName, emptyList())

    teamReposity.create(team)
  }
}
