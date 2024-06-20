package de.lausi95.misterx.backend.application.usecase

data class CreateTeamCommand(
  val teamName: String
)

interface CreateTeamUsecase {

  fun createTeam(command: CreateTeamCommand)
}