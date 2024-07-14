package de.lausi95.misterx.backend.adapter.startup

import de.lausi95.misterx.backend.application.service.UserApplicationService
import de.lausi95.misterx.backend.domain.model.user.*
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EnsureAdminUser(
  private val userRepository: UserRepository,
  private val userApplicationService: UserApplicationService,
) {

  private final val log = LoggerFactory.getLogger("admin user")
  private final val defaultAdminUsername = "admin"

  @EventListener(ApplicationStartedEvent::class)
  fun registerAdminUser() {
    if (userRepository.existsByUsername(defaultAdminUsername)) {
      return
    }

    log.info("creating admin user!")

    userApplicationService.registerAdmin("admin", "admin", "admin", "admin1234!")
  }
}
