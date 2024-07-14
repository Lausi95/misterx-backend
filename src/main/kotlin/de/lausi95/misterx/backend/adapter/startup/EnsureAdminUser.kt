package de.lausi95.misterx.backend.adapter.startup

import de.lausi95.misterx.backend.application.service.UserApplicationService
import de.lausi95.misterx.backend.domain.model.misterx.Misterx
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.user.*
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EnsureAdminUser(
  private val userRepository: UserRepository,
  private val userApplicationService: UserApplicationService,
  private val misterxRepository: MisterxRepository,
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

    userApplicationService.registerUser("wasser", "wasser", "wasser", "wasser")
    val wasserUser = userRepository.findByUsername("wasser").orElseThrow()
    misterxRepository.save(Misterx(UUID.randomUUID(), wasserUser.id, "nfdsfjkldsafjkdsl;"))
  }
}
