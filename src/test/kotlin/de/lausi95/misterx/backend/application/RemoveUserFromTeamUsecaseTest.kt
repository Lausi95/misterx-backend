package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.application.usecase.RemoveUserFromTeamCommand
import de.lausi95.misterx.backend.application.usecase.RemoveUserFromTeamUsecase
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import de.lausi95.misterx.backend.randomTeam
import de.lausi95.misterx.backend.randomUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.fail
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
    val someTeam = randomTeam(members = mutableSetOf(someUser.id))

    userRepository.save(someUser)
    teamRepository.save(someTeam)

    removeUserFromTeamUsecase.removeUserFromTeam(RemoveUserFromTeamCommand(someTeam.teamId, someUser.id))

    val team = teamRepository.findById(someTeam.teamId)
    assertThat(team.containsMember(someUser.id)).isFalse()
  }

  @Test
  fun shouldThrowWhenTeamDoesNotExist() {
    val someUser = randomUser()
    val someTeam = randomTeam(members = mutableSetOf(someUser.id))

    userRepository.save(someUser)

    val ex = assertThrows(DomainException::class.java) {
      removeUserFromTeamUsecase.removeUserFromTeam(RemoveUserFromTeamCommand(someTeam.teamId, someUser.id))
    }
    assertThat(ex).hasMessage("Team with id ${someTeam.teamId} does not exist.")
  }
}
