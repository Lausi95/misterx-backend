package de.lausi95.misterx.backend.domain.model

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

data class MapCorner(
  val latitude: Double,
  val longitude: Double,
)

data class CellConfig(
  val latitudeCells: Int,
  val longitudeCells: Int,
)

data class MapConfig(
  val id: UUID,
  var active: Boolean,
  var corner1: MapCorner,
  var corner2: MapCorner,
  var cells: CellConfig,
  var message: String,
)

interface MapConfigRepository: MongoRepository<MapConfig, UUID> {

  fun findByActiveIsTrue(): List<MapConfig>
}

data class Map(
  val config: MapConfig,
  val locations: List<Location>
)
