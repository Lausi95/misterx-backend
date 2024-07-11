package de.lausi95.misterx.backend.adapter.web.misterx

import de.lausi95.misterx.backend.domain.model.Location
import de.lausi95.misterx.backend.domain.model.LocationRepository
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDateTime
import java.util.UUID

@Controller
@RequestMapping("/misterx")
class MisterxController(
  private val userRepository: UserRepository,
  private val misterxRepository: MisterxRepository,
  private val locationRepository: LocationRepository) {

  @GetMapping("/check")
  @ResponseBody
  fun checkMisterx(): ResponseEntity<String> {
    val username = SecurityContextHolder.getContext()?.authentication?.name
    if (username == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("\"Authentication required\"")
    }

    val user = userRepository.findByUsername(username).orElse(null)
    if (user == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("\"User not found\"")
    }

    val misterx = misterxRepository.findByUserId(user.id).orElse(null)
    if (misterx == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("\"You are no misterx!\"")
    }

    return ResponseEntity.ok("\"logged in.\"")
  }

  @PostMapping("/location")
  @ResponseBody
  fun updateLocation(@RequestBody request: UpdateLocationRequest): ResponseEntity<String> {
    val username = SecurityContextHolder.getContext()?.authentication?.name
    if (username == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("no username?")
    }

    val user = userRepository.findByUsername(username).orElse(null)
    if (user == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user not found")
    }

    val misterx = misterxRepository.findByUserId(user.id).orElse(null)
    if (misterx == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("no misterx.")
    }

    val location = Location(
      UUID.randomUUID(),
      misterx.id,
      request.latitude,
      request.longitude,
      LocalDateTime.now()
    )

    locationRepository.save(location)
    return ResponseEntity.ok(misterx.token)
  }
}

data class UpdateLocationRequest(
  val latitude: Double,
  val longitude: Double,
)
