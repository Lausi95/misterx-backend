package de.lausi95.misterx.backend.domain.model.user

import java.util.*

data class UserId(val value: UUID) {
  companion object {
    fun generate(): UserId = UserId(UUID.randomUUID())
  }

  constructor(value: String): this(UUID.fromString(value))
}

data class Username(val value: String)

data class Firstname(val value: String)

data class Lastname(val value: String)

data class PasswordHash(val value: String)

data class User(
  val id: UserId,
  val username: Username,
  val firstname: Firstname,
  val lastname: Lastname,
  val passwordHash: PasswordHash,
  val admin: Boolean,
)

interface UserRepository {

  fun create(user: User)

  fun save(user: User)

  fun findByUsername(username: Username): User?

  fun existsById(userId: UserId): Boolean

  fun existsByUsername(username: Username): Boolean
}
