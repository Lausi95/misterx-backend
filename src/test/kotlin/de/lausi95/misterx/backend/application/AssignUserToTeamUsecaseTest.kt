package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.application.usecase.AssignUserToTeamCommand
import de.lausi95.misterx.backend.application.usecase.AssignUserToTeamUsecase
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import de.lausi95.misterx.backend.randomTeam
import de.lausi95.misterx.backend.randomUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AssignUserToTeamUsecaseTest {

  @Autowired
  private lateinit var assignUserToTeamUsecase: AssignUserToTeamUsecase

  @Autowired
  private lateinit var userRepository: UserRepository

  @Autowired
  private lateinit var teamRepository: TeamRepository

  @Test
  fun shouldAssignUserToTeam() {
    val someUser = randomUser()
    val someTeam = randomTeam()

    userRepository.save(someUser)
    teamRepository.save(someTeam)

    assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someTeam.teamId, someUser.id))

    val team = teamRepository.findById(someTeam.teamId)
    assertThat(team.containsMember(someUser.id)).isTrue()
  }

  @Test
  fun shouldThrowWhenTryingToAssignMemberToTeamWhichIsAlreadyInAnotherTeam() {
    val someUser = randomUser()
    val someTeam = randomTeam()
    val someOtherTeam = randomTeam(members = mutableSetOf(someUser.id))

    userRepository.save(someUser)
    teamRepository.save(someTeam)
    teamRepository.save(someOtherTeam)

    val ex = assertThrows(DomainException::class.java) {
      assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someTeam.teamId, someUser.id))
    }
    assertThat(ex).hasMessage("User with userId ${someUser.id} is already assigned to a different team.")
  }

  @Test
  fun shouldThrowWhenTryingToAssignNotExistingUser() {
    val someUser = randomUser()
    val someTeam = randomTeam()

    teamRepository.save(someTeam)

    val ex = assertThrows(DomainException::class.java) {
      assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someTeam.teamId, someUser.id))
    }
    assertThat(ex).hasMessage("User with userId ${someUser.id} does not exist.")
  }

  @Test
  fun shouldThrowWhenTryingToAssignToANotExistingTeam() {
    val someUser = randomUser()
    val someTeam = randomTeam()

    userRepository.save(someUser)

    val ex = assertThrows(DomainException::class.java) {
      assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someTeam.teamId, someUser.id))
    }
    assertThat(ex).hasMessage("Team with id ${someTeam.teamId} does not exist.")
  }
}
