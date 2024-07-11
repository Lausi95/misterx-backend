package de.lausi95.misterx.backend.adapter.startup

import de.lausi95.misterx.backend.application.service.UserApplicationService
import de.lausi95.misterx.backend.domain.model.misterx.Misterx
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.user.*
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EnsureAdminUser(
  private val userRepository: UserRepository,
  private val userApplicationService: UserApplicationService,
  private val passwordEncoder: PasswordEncoder,
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

    val passwordHash = passwordEncoder.encode(defaultAdminUsername)
    userApplicationService.registerAdmin("admin", "admin", "admin", passwordHash)

    val user = userRepository.findByUsername("admin").orElseThrow()
    if (!misterxRepository.existsByUserId(user.id)) {
      val misterx = Misterx(UUID.randomUUID(), user.id, Misterx.createToken())
      misterxRepository.save(misterx)
    }
  }
}