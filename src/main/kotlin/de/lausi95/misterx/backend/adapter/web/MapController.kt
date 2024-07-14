package de.lausi95.misterx.backend.adapter.web

import de.lausi95.misterx.backend.domain.model.*
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.UUID

data class UpdateMapConfigRequest(
  val corner1Latitude: Double,
  val corner1Longitude: Double,
  val corner2Latitude: Double,
  val corner2Longitude: Double,
  val gridLatitude: Int,
  val gridLongitude: Int,
  val message: String,
)

data class LocationEntry(val latitude: Double, val longitude: Double)

data class LocationModel(
  val misterxLocations: List<LocationEntry>,
  val waterLocation: LocationEntry,
)

@Controller
class MapController(
  private val userRepository: UserRepository,
  private val teamRepository: TeamRepository,
  private val misterxRepository: MisterxRepository,
  private val locationRepository: LocationRepository,
  private val mapConfigRepository: MapConfigRepository,
  private val modelService: ModelService
) {

  @GetMapping("/app")
  fun getApp(): String {
    return "redirect:https://drive.google.com/file/d/1pRAnByF7XNSxNOD_oVP_qNNbrNPDwBOx/view?usp=sharing"
  }

  @GetMapping("/")
  fun getMap(model: Model): String {

    return with(modelService) {
      model.page("map") {}
    }
  }

  @PostMapping("/map-config")
  fun postMapConfiguration(request: UpdateMapConfigRequest): String {
    var config = mapConfigRepository.findByActiveIsTrue().firstOrNull()
    if (config == null) {
      config = MapConfig(
        UUID.randomUUID(),
        true,
        MapCorner(request.corner1Latitude, request.corner1Longitude),
        MapCorner(request.corner2Latitude, request.corner2Longitude),
        CellConfig(request.gridLatitude, request.gridLongitude),
        request.message
      )
    } else {
      config = MapConfig(
        config.id,
        config.active,
        MapCorner(request.corner1Latitude, request.corner1Longitude),
        MapCorner(request.corner2Latitude, request.corner2Longitude),
        CellConfig(request.gridLatitude, request.gridLongitude),
        request.message
      )
    }
    mapConfigRepository.save(config)
    return "redirect:/map-config"
  }

  @GetMapping("/map-config")
  fun getMapConfiguration(model: Model): String {
    val config = mapConfigRepository.findByActiveIsTrue().firstOrNull() ?: MapConfig(
      UUID.randomUUID(),
      true,
      MapCorner(0.0, 0.0),
      MapCorner(0.0, 0.0),
      CellConfig(0, 0),
      ""
    )

    return with(modelService) {
      model.page("map-config") {
        it.addAttribute("conf", config)
      }
    }
  }

  @GetMapping("/api/map")
  @ResponseBody
  fun getApiMapConfiguration(): MapConfig {
    return mapConfigRepository.findByActiveIsTrue().first()
  }

  @GetMapping("/api/locations")
  @ResponseBody
  fun getLocations(): LocationModel {
    val waterLocation = LocationEntry(0.0, 0.0)

    val username = SecurityContextHolder.getContext()?.authentication?.name
    if (username == null) {
      val misterx = misterxRepository.findAll()

       val misterxLocations = misterx.mapNotNull {
        locationRepository.findByMisterxIdOrderByTimeDesc(
          Pageable.ofSize(1),
          it.id
        ).content.firstOrNull()
      }.map {
        LocationEntry(it.latitude, it.longiture)
       }

      return LocationModel(misterxLocations, waterLocation)
    }

    // use index for that?
    // one index user -> team
    // other index team -> map
    val foundMisterx = userRepository.findByUsername(username)
      .flatMap { teamRepository.findByMembersContaining(it.id) }
      .map { it.foundMisterx }
      .orElseGet { mutableSetOf() }

    val misterx = misterxRepository.findAll().filter { !foundMisterx.map { fmx -> fmx.misterxId }.contains(it.id) }
    val misterxLocations = misterx.mapNotNull {
      locationRepository.findByMisterxIdOrderByTimeDesc(
        Pageable.ofSize(1),
        it.id
      ).content.firstOrNull()
    }.map {
      LocationEntry(it.latitude, it.longiture)
    }

    return LocationModel(misterxLocations, waterLocation)
  }
}
