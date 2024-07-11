package de.lausi95.misterx.backend.domain.model.misterx

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

data class Misterx(
  @Id val id: UUID,
  val userId: UUID,
  var token: String,
) {

  companion object {
    private val charPool = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVXYZ".toCharArray()
    fun createToken(): String {
      return (1..3)
        .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")
    }
  }
}

interface MisterxRepository : MongoRepository<Misterx, UUID> {

  fun findByToken(token: String): Optional<Misterx>

  fun findByUserId(userId: UUID): Optional<Misterx>

  fun existsByUserId(userId: UUID): Boolean
}
