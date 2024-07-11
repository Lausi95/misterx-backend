package de.lausi95.misterx.backend.adapter.web

import de.lausi95.misterx.backend.domain.model.Location
import de.lausi95.misterx.backend.domain.model.LocationRepository
import de.lausi95.misterx.backend.domain.model.MapConfig
import de.lausi95.misterx.backend.domain.model.MapConfigRepository
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class MapController(
  private val userRepository: UserRepository,
  private val teamRepository: TeamRepository,
  private val misterxRepository: MisterxRepository,
  private val locationRepository: LocationRepository,
  private val mapConfigRepository: MapConfigRepository,
) {

  @GetMapping("/app")
  fun getApp(): String {
    return "redirect:https://drive.google.com/file/d/1pRAnByF7XNSxNOD_oVP_qNNbrNPDwBOx/view?usp=sharing"
  }

  @GetMapping("/")
  fun getMap(model: Model): String {
    model.addAttribute("currentPage", "map")
    model.addAttribute("view", "views/map")
    return "page"
  }

  @GetMapping("/map")
  @ResponseBody
  fun getMapConfiguration(): MapConfig {
    return mapConfigRepository.findByActiveIsTrue().first()
  }

  @GetMapping("/locations")
  @ResponseBody
  fun getLocations(): List<Location> {
    val username = SecurityContextHolder.getContext()?.authentication?.name
    if (username == null) {
      val misterx = misterxRepository.findAll()
      return misterx.mapNotNull {
        locationRepository.findByMisterxIdOrderByTimeDesc(
          Pageable.ofSize(1),
          it.id
        ).content.firstOrNull()
      }
    }

    // use index for that?
    // one index user -> team
    // other index team -> map
    val foundMisterx = userRepository.findByUsername(username)
      .flatMap { teamRepository.findByMembersContaining(it.id) }
      .map { it.foundMisterx }
      .orElseGet { mutableSetOf() }

    val misterx = misterxRepository.findAll().filter { foundMisterx.map { fmx -> fmx.misterxId }.contains(it.id) }
    val locations = misterx.mapNotNull {
      locationRepository.findByMisterxIdOrderByTimeDesc(
        Pageable.ofSize(1),
        it.id
      ).content.firstOrNull()
    }

    return locations
  }
}
