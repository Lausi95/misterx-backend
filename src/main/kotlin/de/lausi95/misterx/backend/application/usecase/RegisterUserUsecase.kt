package de.lausi95.misterx.backend.application.usecase

data class RegisterUserCommand(
  val username: String,
  val firstname: String,
  val lastname: String,
  val passwordHash: String,
)

interface RegisterUserUsecase {

  fun registerUser(command: RegisterUserCommand)
}
