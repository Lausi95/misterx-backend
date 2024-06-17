package de.lausi95.misterx.backend.adapter.persistence

import de.lausi95.misterx.backend.domain.model.team.Team
import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.team.TeamName
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserId
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Component

@Component
class PostgresqlTeamRepository(private val jdbcTemplate: JdbcTemplate) : TeamRepository {

  private val log = LoggerFactory.getLogger(PostgresqlTeamRepository::class.java)

  override fun create(team: Team) {
    log.debug("inserting team into database: {}", team)
    jdbcTemplate.update(
      "INSERT INTO team (id, name) VALUES (?, ?)",
      team.id.value,
      team.name.value
    )
  }

  override fun save(team: Team) {
    val dbTeam = findById(team.id) ?: error("Cannot update team. Team does not exist.")

    // update team name if changed
    if (dbTeam.name != team.name) {
      log.debug("Updating team in database: {}", team)
      jdbcTemplate.update(
        "UPDATE team SET name = ? WHERE id = ?",
        team.name.value,
        team.id.value
      )
    }

    // remove members if there are not in the members liste anymore
    dbTeam.members.filter { !team.members.contains(it) }.forEach {
      log.debug("Deleting team member {} from team {} in database", it, team)
      jdbcTemplate.update(
        "DELETE FROM team_user WHERE team_id = ? AND user_id = ?",
        team.id.value,
        it.value
      )
    }

    // add members if there are in the members list, but not in the db
    team.members.filter { !dbTeam.members.contains(it) }.forEach {
      log.debug("Adding team member {} to team {} in database", it, team)
      jdbcTemplate.update(
        "INSERT INTO team_user (team_id, user_id) VALUES (?, ?)",
        team.id.value,
        it.value
      )
    }
  }

  override fun findById(teamId: TeamId): Team? {
    log.debug("Loading team with id {} from database", teamId)
    return jdbcTemplate.query<Team>(
      "SELECT id, name FROM team WHERE team.id = ?",
      { rs, _ ->
        val memberList = findUserIdsByTeamId(teamId)
        val teamName = TeamName(rs.getString("name"))
        Team(teamId, teamName, memberList)
      },
      teamId.value
    ).firstOrNull()
  }

  override fun findAll(): List<Team> {
    log.debug("Loading all teams from database")
    return jdbcTemplate.query(
      "SELECT id, name FROM team",
    ) { rs, _ ->
      val teamId = TeamId(rs.getString("id"))
      val teamName = TeamName(rs.getString("name"))
      val memberList = findUserIdsByTeamId(teamId)
      Team(teamId, teamName, memberList)
    }
  }

  private fun findUserIdsByTeamId(teamId: TeamId): MutableList<UserId> {
    log.debug("Loading members of team {} from database", teamId)
    return jdbcTemplate.query(
      "SELECT user_id FROM team_user WHERE team_id = ?",
      { rs2, _ ->
        val userId = UserId(rs2.getString("user_id"))
        userId
      },
      teamId.value
    )
  }

  override fun existsByTeamName(teamName: TeamName): Boolean {
    val count = jdbcTemplate.queryForObject<Int>(
      "SELECT COUNT(*) FROM team WHERE name = ?",
      arrayOf(teamName.value)
    ) ?: 0
    return count > 0
  }
}