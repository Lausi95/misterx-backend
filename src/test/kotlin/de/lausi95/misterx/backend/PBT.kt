package de.lausi95.misterx.backend

import de.lausi95.misterx.backend.domain.model.team.Team
import de.lausi95.misterx.backend.domain.model.team.TeamId
import de.lausi95.misterx.backend.domain.model.user.User
import de.lausi95.misterx.backend.domain.model.user.UserId
import kotlin.random.Random

private val charPool = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVXYZ".toCharArray()

fun randomString(len: Int = 10) = (1..len)
  .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
  .joinToString("")

fun randomTeamName(len: Int = 10) = randomString(len)

fun randomUsername(len: Int = 10) = randomString(len)

fun randomFirstname(len: Int = 10) = randomString(len)

fun randomLastname(len: Int = 10) = randomString(len)

fun randomPasswordHash(len: Int = 10) = randomString(len)

fun randomTeam(members: MutableSet<UserId> = mutableSetOf()) = Team(TeamId.generate(), randomTeamName(10), members)

fun randomUser() = User(UserId.generate(), randomUsername(), randomFirstname(), randomLastname(), randomPasswordHash(), false)
