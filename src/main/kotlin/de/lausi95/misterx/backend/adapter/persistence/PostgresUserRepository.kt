package de.lausi95.misterx.backend.adapter.persistence

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.domain.model.user.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Component

@Component
class PostgresUserRepository(private val jdbcTemplate: JdbcTemplate) : UserRepository {

  override fun save(user: User) {
    if (!existsById(user.id)) {
      jdbcTemplate.update(
        """INSERT INTO "user" (id, username, firstname, lastname, password_hash, admin) VALUES (?, ?, ?, ?, ?, ?)""",
        user.id.value,
        user.username,
        user.firstname,
        user.lastname,
        user.passwordHash,
        user.admin,
      )
    } else {
      jdbcTemplate.update(
        """UPDATE "user" SET username = ?, firstname = ?, lastname = ?, password_hash = ? WHERE id = ?""",
        user.username,
        user.firstname,
        user.lastname,
        user.passwordHash,
        user.id.value,
      )
    }
  }

  override fun findByUsername(username: String): User {
    return jdbcTemplate.query("""SELECT * FROM "user" WHERE username = ?""",
      { rc, _ ->
        User(
          UserId(rc.getString("id")),
          rc.getString("username"),
          rc.getString("firstname"),
          rc.getString("lastname"),
          rc.getString("password_hash"),
          rc.getBoolean("admin")
        )
      }, username
    ).firstOrNull() ?: throw DomainException("User with username $username does not exist.")
  }

  override fun findById(userId: UserId): User {
    return jdbcTemplate.query("""SELECT * FROM "user" WHERE id = ?""",
      { rc, _ ->
        User(
          UserId(rc.getString("id")),
          rc.getString("username"),
          rc.getString("firstname"),
          rc.getString("lastname"),
          rc.getString("password_hash"),
          rc.getBoolean("admin")
        )
      }, userId.value
    ).firstOrNull() ?: throw DomainException("User with id $userId does not exist.")
  }

  override fun existsById(userId: UserId): Boolean {
    val count = jdbcTemplate.queryForObject<Int>(
      "SELECT COUNT(*) FROM \"user\" WHERE id = ?",
      arrayOf(userId.value)
    ) ?: 0
    return count > 0
  }

  override fun existsByUsername(username: String): Boolean {
    val count = jdbcTemplate.queryForObject<Int>(
      "SELECT COUNT(*) FROM \"user\" WHERE username = ?",
      arrayOf(username)
    ) ?: 0
    return count > 0
  }
}
