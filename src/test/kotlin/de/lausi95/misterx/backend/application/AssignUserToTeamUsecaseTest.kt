package de.lausi95.misterx.backend.application

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

    userRepository.create(someUser)
    teamRepository.create(someTeam)

    assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someUser.id, someTeam.id))

    val team = teamRepository.findById(someTeam.id) ?: fail("Team does not exist.")
    assertThat(team.containsMember(someUser.id)).isTrue()
  }

  @Test
  fun shouldThrowWhenTryingToAssignMemberToTeamAlreadyAssignedTo() {
    val someUser = randomUser()
    val someTeam = randomTeam(members = listOf(someUser.id))

    userRepository.create(someUser)

    teamRepository.create(someTeam)
    teamRepository.save(someTeam)

    val ex = assertThrows(AssignUserToTeamException::class.java) { assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someUser.id, someTeam.id)) }
    assertThat(ex).hasMessage("Cannot assign user to team: User with userId ${someUser.id} is already assigned to the team with id ${someTeam.id}.")
  }

  @Test
  fun shouldThrowWhenTryingToAssignMemberToTeamWhichIsAlreadyInAnotherTeam() {
    val someUser = randomUser()
    val someTeam = randomTeam()
    val someOtherTeam = randomTeam(members = listOf(someUser.id))

    userRepository.create(someUser)
    teamRepository.create(someTeam)
    teamRepository.create(someOtherTeam)
    teamRepository.save(someOtherTeam)

    val ex = assertThrows(AssignUserToTeamException::class.java) { assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someUser.id, someTeam.id)) }
    assertThat(ex).hasMessage("Cannot assign user to team: User with userId ${someUser.id} is already assigned to a different team.")
  }

  @Test
  fun shouldThrowWhenTryingToAssignNotExistingUser() {
    val someUser = randomUser()
    val someTeam = randomTeam()

    teamRepository.create(someTeam)

    val ex = assertThrows(AssignUserToTeamException::class.java) { assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someUser.id, someTeam.id)) }
    assertThat(ex).hasMessage("Cannot assign user to team: User with userId ${someUser.id} does not exist.")
  }

  @Test
  fun shouldThrowWhenTryingToAssignToANotExistingTeam() {
    val someUser = randomUser()
    val someTeam = randomTeam()

    userRepository.create(someUser)

    val ex = assertThrows(AssignUserToTeamException::class.java) { assignUserToTeamUsecase.assignUserToTeam(AssignUserToTeamCommand(someUser.id, someTeam.id)) }
    assertThat(ex).hasMessage("Cannot assign user to team: Team with teamId ${someTeam.id} does not exist.")
  }
}
