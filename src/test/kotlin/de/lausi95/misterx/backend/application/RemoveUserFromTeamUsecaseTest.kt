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
class RemoveUserFromTeamUsecaseTest {

  @Autowired
  private lateinit var removeUserFromTeamUsecase: RemoveUserFromTeamUsecase

  @Autowired
  private lateinit var userRepository: UserRepository

  @Autowired
  private lateinit var teamRepository: TeamRepository

  @Test
  fun shouldRemoveUserFromTeam() {
    val someUser = randomUser()
    val someTeam = randomTeam(members = listOf(someUser.id))

    userRepository.create(someUser)
    teamRepository.create(someTeam)
    teamRepository.save(someTeam)

    removeUserFromTeamUsecase.removeUserFromTeam(RemoveUserFromTeamCommand(someUser.id, someTeam.id))

    val team = teamRepository.findById(someTeam.id) ?: fail("Team does not exist")
    assertThat(team.containsMember(someUser.id)).isFalse()
  }

  @Test
  fun shouldThrowWhenTeamDoesNotExist() {
    val someUser = randomUser()
    val someTeam = randomTeam(members = listOf(someUser.id))

    userRepository.create(someUser)

    val ex = assertThrows(RemoveUserFromTeamException::class.java) { removeUserFromTeamUsecase.removeUserFromTeam(RemoveUserFromTeamCommand(someUser.id, someTeam.id)) }
    assertThat(ex).hasMessage("Cannot remove user from team: Team with id ${someTeam.id} does not exist.")
  }

  @Test
  fun shouldThrowWhenUserDoesNotExist() {
    val someUser = randomUser()
    val someTeam = randomTeam(members = listOf(someUser.id))

    teamRepository.create(someTeam)

    val ex = assertThrows(RemoveUserFromTeamException::class.java) { removeUserFromTeamUsecase.removeUserFromTeam(RemoveUserFromTeamCommand(someUser.id, someTeam.id)) }
    assertThat(ex).hasMessage("Cannot remove user from team: User with id ${someUser.id} does not exist.")
  }

  @Test
  fun shouldThrowWhenUserIsNotInThisTeam() {
    val someUser = randomUser()
    val someTeam = randomTeam()

    userRepository.create(someUser)
    teamRepository.create(someTeam)

    val ex = assertThrows(RemoveUserFromTeamException::class.java) { removeUserFromTeamUsecase.removeUserFromTeam(RemoveUserFromTeamCommand(someUser.id, someTeam.id)) }
    assertThat(ex).hasMessage("Cannot remove user from team: User with id ${someUser.id} is not in the team with id ${someTeam.id}.")
  }
}
