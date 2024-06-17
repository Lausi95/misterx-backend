package de.lausi95.misterx.backend.adapter.persistence

import de.lausi95.misterx.backend.domain.model.user.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Component

@Component
class PostgresUserRepository(private val jdbcTemplate: JdbcTemplate) : UserRepository {

  override fun create(user: User) {
    jdbcTemplate.update(
      """INSERT INTO "user" (id, username, firstname, lastname, password_hash, admin) VALUES (?, ?, ?, ?, ?, ?)""",
      user.id.value,
      user.username.value,
      user.firstname.value,
      user.lastname.value,
      user.passwordHash.value,
      user.admin,
    )
  }

  override fun save(user: User) {
    jdbcTemplate.update(
      """UPDATE "user" SET username = ?, firstname = ?, lastname = ?, password_hash = ? WHERE id = ?""",
      user.username.value,
      user.firstname.value,
      user.lastname.value,
      user.passwordHash.value,
      user.id.value,
    )
  }

  override fun findByUsername(username: Username): User? {
    return jdbcTemplate.query("""SELECT * FROM "user" WHERE username = ?""",
      { rc, _ ->
        User(
          UserId(rc.getString("id")),
          Username(rc.getString("username")),
          Firstname(rc.getString("firstname")),
          Lastname(rc.getString("lastname")),
          PasswordHash(rc.getString("password_hash")),
          rc.getBoolean("admin")
        )
      }, username.value
    ).firstOrNull()
  }

  override fun existsById(userId: UserId): Boolean {
    val count = jdbcTemplate.queryForObject<Int>(
      "SELECT COUNT(*) FROM \"user\" WHERE id = ?",
      arrayOf(userId.value)
    ) ?: 0
    return count > 0
  }

  override fun existsByUsername(username: Username): Boolean {
    val count = jdbcTemplate.queryForObject<Int>(
      "SELECT COUNT(*) FROM \"user\" WHERE username = ?",
      arrayOf(username.value)
    ) ?: 0
    return count > 0
  }
}