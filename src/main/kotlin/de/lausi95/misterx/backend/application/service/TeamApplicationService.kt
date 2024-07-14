package de.lausi95.misterx.backend.application.service

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.domain.model.misterx.Misterx
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.team.FoundMisterx
import de.lausi95.misterx.backend.domain.model.team.Team
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class TeamApplicationService(
  private val teamRepository: TeamRepository,
  private val userRepository: UserRepository,
  private val misterxRepository: MisterxRepository
) {

    private val log = LoggerFactory.getLogger(TeamApplicationService::class.java)

  @Transactional
  fun assignUserToTeam(userId: UUID, teamId: UUID) {
    if (!userRepository.existsById(userId)) {
      throw DomainException("User with userId $userId does not exist.")
    }

    val otherTeams = teamRepository.findAll().filter { it.id != teamId }
    if (otherTeams.any { it.members.contains(userId) }) {
      throw DomainException("User with userId $userId is already assigned to a different team.")
    }

    val team = teamRepository.findById(teamId).orElseThrow {
      error("Team doens not exist.")
    }
    team.members.add(userId)
    teamRepository.save(team)
  }

  @Transactional
  fun removeUserFromTeam(userId: UUID, teamId: UUID) {
    log.info("Remving user {} from team {}.", userId, teamId)

    val team = teamRepository.findById(teamId).orElseThrow {
      error("Team does not exist.")
    }
    team.members.remove(userId)
    teamRepository.save(team)
  }

  fun createTeam(teamName: String) {
    log.info("Creating team with name {}", teamName)

    if (teamRepository.existsByName(teamName)) {
      throw DomainException("Team with name '${teamName}' already exists.")
    }

    val team = Team(UUID.randomUUID(), teamName, mutableSetOf(), mutableSetOf())
    teamRepository.save(team)
  }

  @Transactional
  fun teamFoundMisterx(teamId: UUID, token: String) {
    val team = teamRepository.findById(teamId).orElseThrow {
      error("No team with id $teamId")
    }
    val misterx = misterxRepository.findByToken(token).orElseThrow {
      error("No misterx with token $token")
    }
    if (team.foundMisterx.map { it.misterxId }.contains(misterx.id)) {
      error("Team already found this misterx.")
    }

    team.foundMisterx.add(FoundMisterx(misterx.id, LocalDateTime.now()))
    misterx.token = Misterx.createToken()

    teamRepository.save(team)
    misterxRepository.save(misterx)
  }
}
