package de.lausi95.misterx.backend.adapter.persistence

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.domain.model.team.*
import de.lausi95.misterx.backend.domain.model.user.UserId
import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Component

@Component
class PostgresqlTeamRepository(
  private val jdbcTemplate: JdbcTemplate
) : TeamRepository {

  private val teamEntityRowMapper = RowMapper<Team> { row, _ ->
    val teamId = TeamId(row.getString("id"))
    val teamName = row.getString("name")
    val memberList = findUserIdsByTeamId(teamId)
    Team(teamId, teamName, memberList)
  }

  private val memberIdRowMapper = RowMapper<UserId> { row, _ ->
    UserId(row.getString("user_id"))
  }

  override fun save(team: Team) {
    if (!existsById(team.teamId)) {
      jdbcTemplate.update(
        """
        INSERT INTO 
          team (id, name) 
        VALUES 
          (?, ?) 
        """,
        team.teamId.value,
        team.name
      )
    } else {
      jdbcTemplate.update(
        """UPDATE team SET name = ? WHERE id = ?""",
        team.name,
        team.teamId.value
      )
    }

    val dbEntity = findById(team.teamId)

    // remove members if there are not in the members list anymore
    dbEntity.members.filter { !team.members.contains(it) }.forEach {
      @Language("sql") val deleteMemberSql = """
        DELETE FROM 
          team_user 
        WHERE 
          team_id = ? AND 
          user_id = ? 
      """.trimIndent()

      jdbcTemplate.update(deleteMemberSql, team.teamId.value, it.value)
    }

    // add members if there are in the members list, but not in the db
    team.members.filter { !dbEntity.members.contains(it) }.forEach {
      jdbcTemplate.update(
        """INSERT INTO team_user (team_id, user_id) VALUES (?, ?)""",
        team.teamId.value,
        it.value
      )
    }
  }

  override fun findById(teamId: TeamId): Team {
    @Language("sql")
    val sql = """
      SELECT id, name FROM team WHERE team.id = ?
    """.trimIndent()

    val resultList = jdbcTemplate.query(sql, teamEntityRowMapper, teamId.value)

    return resultList.firstOrNull() ?: throw DomainException("Team with id $teamId does not exist.")
  }

  override fun findAll(): List<Team> {
    @Language("sql")
    val sql = """
      SELECT id, name FROM team
    """.trimIndent()

    return jdbcTemplate.query(sql, teamEntityRowMapper)
  }

  override fun existsById(teamId: TeamId): Boolean {
    @Language("sql") val countTeamByIdSql = """
      SELECT COUNT(*) FROM team WHERE id = ?
    """.trimIndent()

    val count = jdbcTemplate.queryForObject<Long>(countTeamByIdSql, arrayOf(teamId.value))

    return (count ?: 0) > 0
  }

  private fun findUserIdsByTeamId(teamId: TeamId): MutableSet<UserId> {
    @Language("sql")
    val sql = """
      SELECT user_id FROM team_user WHERE team_id = ? 
    """.trimIndent()

    return jdbcTemplate.query(sql, memberIdRowMapper, teamId.value).toMutableSet()
  }

  override fun existsByTeamName(teamName: String): Boolean {
    val count = jdbcTemplate.queryForObject<Int>(
      "SELECT COUNT(*) FROM team WHERE name = ?",
      arrayOf(teamName)
    ) ?: 0
    return count > 0
  }
}
