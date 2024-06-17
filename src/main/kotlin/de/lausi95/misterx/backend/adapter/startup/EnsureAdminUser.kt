package de.lausi95.misterx.backend.adapter.startup

import de.lausi95.misterx.backend.application.RegisterUserCommand
import de.lausi95.misterx.backend.application.RegisterUserUsecase
import de.lausi95.misterx.backend.domain.model.user.*
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EnsureAdminUser(
  private val userRepository: UserRepository,
  private val registerUserUsecase: RegisterUserUsecase,
) {

  @EventListener(ApplicationStartedEvent::class)
  fun registerAdminUser() {
    val adminUsername = Username("admin")
    if (userRepository.existsByUsername(adminUsername)) {
      return
    }

    registerUserUsecase.registerAdminUser(RegisterUserCommand(
      adminUsername,
      Firstname("Admin"),
      Lastname("Admin"),
      PasswordHash("admin")
    ))
  }
}