package de.lausi95.misterx.backend.application.service

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.domain.model.Location
import de.lausi95.misterx.backend.domain.model.LocationRepository
import de.lausi95.misterx.backend.domain.model.misterx.Misterx
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class MisterxApplicationService(
  private val userRepository: UserRepository,
  private val misterxRepository: MisterxRepository,
  private val locationRepository: LocationRepository,
) {

  @Transactional
  fun createMisterx(userId: UUID) {
    if (!userRepository.existsById(userId)) {
      throw DomainException("User with id $userId does not exist.")
    }

    if (misterxRepository.existsByUserId(userId)) {
      throw DomainException("User with id $userId is already a misterx.")
    }

    val misterx = Misterx(UUID.randomUUID(), userId, Misterx.createToken())
    misterxRepository.save(misterx)
  }

  fun updateLocation(misterxId: UUID, latitude: Double, longitude: Double) {
    val location = Location(UUID.randomUUID(), misterxId, latitude, longitude, LocalDateTime.now())
    locationRepository.save(location)
  }
}