package de.lausi95.misterx.backend.application

import de.lausi95.misterx.backend.*
import de.lausi95.misterx.backend.application.usecase.RegisterUserCommand
import de.lausi95.misterx.backend.application.usecase.RegisterUserUsecase
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RegisterUserUsecaseTest {

  @Autowired
  private lateinit var userRepository: UserRepository

  @Autowired
  private lateinit var registerUserUsecase: RegisterUserUsecase

  @Test
  fun shouldRegisterUser() {
    val someUsername = randomUsername()
    val someFirstname = randomFirstname()
    val someLastname = randomLastname()
    val somePasswordHash = randomPasswordHash()

    registerUserUsecase.registerUser(
      RegisterUserCommand(
        someUsername,
        someFirstname,
        someLastname,
        somePasswordHash,
      )
    )

    assertThat(userRepository.existsByUsername(someUsername)).isTrue()
  }

  @Test
  fun shouldThrowWhenTheSameUsernameIsRegisteredMoreThanOnce() {
    val someUsername = randomUsername()
    val someFirstname = randomFirstname()
    val someLastname = randomLastname()
    val somePasswordHash = randomPasswordHash()

    val registerUserCommand = RegisterUserCommand(
      someUsername,
      someFirstname,
      someLastname,
      somePasswordHash,
    )

    registerUserUsecase.registerUser(registerUserCommand)

    val ex = assertThrows(DomainException::class.java) { registerUserUsecase.registerUser(registerUserCommand) }
    assertThat(ex).hasMessage("User with username '$someUsername' already exists.")
  }
}