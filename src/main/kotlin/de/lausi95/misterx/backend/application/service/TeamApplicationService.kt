package de.lausi95.misterx.backend.application.service

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.application.usecase.*
import de.lausi95.misterx.backend.domain.model.team.Team
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class TeamApplicationService(
  private val teamRepository: TeamRepository,
  private val userRepository: UserRepository,
):
  AssignUserToTeamUsecase,
  RemoveUserFromTeamUsecase,
  CreateTeamUsecase {

  @Transactional
  override fun assignUserToTeam(command: AssignUserToTeamCommand) {
    if (!userRepository.existsById(command.userId)) {
      throw DomainException("User with userId ${command.userId} does not exist.")
    }

    val otherTeams = teamRepository.findAll().filter { it.teamId != command.teamId }
    if (otherTeams.any { it.containsMember(command.userId) }) {
      throw DomainException("User with userId ${command.userId} is already assigned to a different team.")
    }

    val team = teamRepository.findById(command.teamId)
    team.assignMember(command.userId)
    teamRepository.save(team)
  }

  @Transactional
  override fun removeUserFromTeam(command: RemoveUserFromTeamCommand) {
    val team = teamRepository.findById(command.teamId)
    team.removeMember(command.userId)
    teamRepository.save(team)
  }

  @Transactional
  override fun createTeam(command: CreateTeamCommand) {
    if (teamRepository.existsByTeamName(command.teamName)) {
      throw DomainException("Team with name '${command.teamName}' already exists.")
    }

    val team = Team.of(command.teamName)
    teamRepository.save(team)
  }
}
