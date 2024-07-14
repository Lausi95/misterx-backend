package de.lausi95.misterx.backend.application.service

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.domain.model.user.User
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserApplicationService(
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder,
) {

  @Transactional
  fun registerUser(username: String, firstname: String, lastname: String, password: String) {
    if (userRepository.existsByUsername(username)) {
      throw DomainException("User with username '$username' already exists.")
    }

    val passwordHash = passwordEncoder.encode(password)

    val user = User(
      UUID.randomUUID(),
      username,
      firstname,
      lastname,
      passwordHash,
      false,
    )

    userRepository.save(user)
  }

  @Transactional
  fun registerAdmin(username: String, firstname: String, lastname: String, passwordHash: String) {
    if (userRepository.existsByUsername(username)) {
      throw DomainException("User with name $username alredy exists.")
    }

    val user = User(
      UUID.randomUUID(),
      username,
      firstname,
      lastname,
      passwordHash,
      true
    )

    userRepository.save(user)
  }
}