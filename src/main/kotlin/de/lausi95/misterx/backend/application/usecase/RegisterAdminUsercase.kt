package de.lausi95.misterx.backend.application.usecase

data class RegisterAdminCommand(
  val username: String,
  val firstname: String,
  val lastname: String,
  val passwordHash: String,
)

interface RegisterAdminUsercase {

  fun registerAdmin(command: RegisterAdminCommand)
}
