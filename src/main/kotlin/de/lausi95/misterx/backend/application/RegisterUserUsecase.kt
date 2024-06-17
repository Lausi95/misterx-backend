package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.domain.model.user.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class RegisterUserCommand(
  val username: Username,
  val firstname: Firstname,
  val lastname: Lastname,
  val passwordHash: PasswordHash,
)

class RegisterUserException : Exception {
  constructor(message: String?) : super("Cannot register user: $message")
  constructor(cause: Throwable?) : super("Cannot register user.", cause)
}

@Service
class RegisterUserUsecase(private val userRepository: UserRepository) {

  @Transactional
  fun registerUser(command: RegisterUserCommand) {
    try {
      tryRegisterUser(command, false)
    } catch (ex: RegisterUserException) {
      throw ex
    } catch (ex: Exception) {
      throw RegisterUserException(ex)
    }
  }

  @Transactional
  fun registerAdminUser(command: RegisterUserCommand) {
    try {
      tryRegisterUser(command, true)
    } catch (ex: RegisterUserException) {
      throw ex
    } catch (ex: Exception) {
      throw RegisterUserException(ex)
    }
  }

  private fun tryRegisterUser(command: RegisterUserCommand, admin: Boolean) {
    if (userRepository.existsByUsername(command.username)) {
      throw RegisterUserException("User with username ${command.username} already exists.")
    }

    val userId = UserId.generate()
    val user = User(
      userId,
      command.username,
      command.firstname,
      command.lastname,
      command.passwordHash,
      admin,
    )

    userRepository.create(user)
  }
}
