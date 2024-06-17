package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.randomTeamName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CreateTeamUsecaseTest {

  @Autowired
  private lateinit var teamRepository: TeamRepository

  @Autowired
  private lateinit var createTeamUsecase: CreateTeamUsecase

  @Test
  fun shouldCreateTeam() {
    val someTeamName = randomTeamName()

    createTeamUsecase.createTeam(CreateTeamCommand(someTeamName))

    assertThat(teamRepository.existsByTeamName(someTeamName)).isTrue()
  }

  @Test
  fun shouldThrowWhenTeamIsTriedToBeCreatedTwice() {
    val someTeamName = randomTeamName()

    createTeamUsecase.createTeam(CreateTeamCommand(someTeamName))

    val ex = Assertions.assertThrows(CreateTeamException::class.java) {
      createTeamUsecase.createTeam(CreateTeamCommand(someTeamName))
    }
    assertThat(ex).hasMessage("Cannot create team: Team with name '$someTeamName' already exists.")
  }
}
