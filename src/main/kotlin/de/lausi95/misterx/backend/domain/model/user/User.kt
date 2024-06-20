package de.lausi95.misterx.backend.domain.model.user

import java.util.*

data class UserId(val value: UUID) {
  companion object {
    fun generate(): UserId = UserId(UUID.randomUUID())
  }

  constructor(value: String): this(UUID.fromString(value))
}

class User(
  var id: UserId,
  var username: String,
  var firstname: String,
  var lastname: String,
  var passwordHash: String,
  var admin: Boolean,
) {

  companion object {

    fun create(username: String, firstname: String, lastname: String, passwordHash: String): User {
      val userId = UserId.generate()
      return User(userId, username, firstname, lastname, passwordHash, false)
    }

    fun createAdmin(username: String, firstname: String, lastname: String, passwordHash: String): User {
      val userId = UserId.generate()
      return User(userId, username, firstname, lastname, passwordHash, true)
    }
  }
}

interface UserRepository {

  fun existsById(userId: UserId): Boolean

  fun existsByUsername(username: String): Boolean

  fun findById(userId: UserId): User

  fun findByUsername(username: String): User

  fun save(user: User)
}
