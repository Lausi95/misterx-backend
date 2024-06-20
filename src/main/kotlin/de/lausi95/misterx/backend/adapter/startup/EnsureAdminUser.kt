package de.lausi95.misterx.backend.adapter.startup

import de.lausi95.misterx.backend.application.usecase.RegisterAdminCommand
import de.lausi95.misterx.backend.application.usecase.RegisterAdminUsercase
import de.lausi95.misterx.backend.domain.model.user.*
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EnsureAdminUser(
  private val userRepository: UserRepository,
  private val registerAdminUsercase: RegisterAdminUsercase
) {

  private final val defaultAdminUsername = "admin"

  @EventListener(ApplicationStartedEvent::class)
  fun registerAdminUser() {
    if (userRepository.existsByUsername(defaultAdminUsername)) {
      return
    }

    registerAdminUsercase.registerAdmin(
      RegisterAdminCommand(
        defaultAdminUsername,
        "Admin",
        "Admin",
        "admin"
      )
    )
  }
}