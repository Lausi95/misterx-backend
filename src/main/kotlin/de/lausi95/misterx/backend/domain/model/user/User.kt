package de.lausi95.misterx.backend.domain.model.user

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

class User(
  var id: UUID,
  var username: String,
  var firstname: String,
  var lastname: String,
  var passwordHash: String,
  var admin: Boolean,
)

interface UserRepository: MongoRepository<User, UUID> {

  fun existsByUsername(username: String): Boolean

  fun findByUsername(username: String): Optional<User>

  fun save(user: User)
}
