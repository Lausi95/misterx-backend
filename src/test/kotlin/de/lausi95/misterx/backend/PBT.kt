package de.lausi95.misterx.backend

import de.lausi95.misterx.backend.domain.model.team.Team
import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.team.TeamName
import de.lausi95.misterx.backend.domain.model.user.*
import kotlin.random.Random

private val charPool = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVXYZ".toCharArray()

fun randomString(len: Int = 10) = (1..len)
  .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
  .joinToString("")

fun randomTeamName(len: Int = 10) = TeamName(randomString(len))

fun randomUsername(len: Int = 10) = Username(randomString(len))

fun randomFirstname(len: Int = 10) = Firstname(randomString(len))

fun randomLastname(len: Int = 10) = Lastname(randomString(len))

fun randomPasswordHash(len: Int = 10) = PasswordHash(randomString(len))

fun randomTeam(members: List<UserId> = emptyList()) = Team(TeamId.generate(), randomTeamName(10), members)

fun randomUser() = User(UserId.generate(), randomUsername(), randomFirstname(), randomLastname(), randomPasswordHash(), false)
