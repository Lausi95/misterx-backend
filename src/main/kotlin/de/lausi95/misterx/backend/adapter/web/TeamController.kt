package de.lausi95.misterx.backend.adapter.web

import de.lausi95.misterx.backend.application.service.MisterxApplicationService
import de.lausi95.misterx.backend.application.service.TeamApplicationService
import de.lausi95.misterx.backend.domain.model.misterx.MisterxRepository
import de.lausi95.misterx.backend.domain.model.team.Team
import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.Comparator

data class FindMisterxRequest(
  val token: String,
)

data class CreateTeamRequest(
  val teamname: String,
  val memberCount: Int,
)

data class JoinTeamRequest(
  val teamId: UUID,
)

data class LeaderboardEntry(
  val position: Int,
  val teamName: String,
  val misterxCount: Int,
  val lastFoundTime: String,
)

@Controller
class TeamController(
  private val teamRepository: TeamRepository,
  private val teamApplicationService: TeamApplicationService,
  private val userRepository: UserRepository,
  private val modelService: ModelService,
  private val misterxApplicationService: MisterxApplicationService,
  private val misterxRepository: MisterxRepository
) {

  @GetMapping("/teams")
  fun getTeams(model: Model): String {
    return with (modelService) {
      model.page("teams") {
        it.addAttribute("teams", teamRepository.findAll())
      }
    }
  }

  @PostMapping("/teams")
  fun postTeam(request: CreateTeamRequest): String {
    teamApplicationService.createTeam(request.teamname, request.memberCount)
    return "redirect:/teams"
  }

  @GetMapping("/join-team")
  fun getJoinTeam(model: Model): String {
    return with(modelService) {
      model.page("join-team") {
        it.addAttribute("teams", teamRepository.findAll())
      }
    }
  }

  @PostMapping("/join-team")
  fun postJoinTeam(request: JoinTeamRequest): String {
    val username = SecurityContextHolder.getContext()?.authentication?.name ?: error("??")
    val user = userRepository.findByUsername(username).orElseThrow()

    teamApplicationService.assignUserToTeam(user.id, request.teamId)

    return "redirect:/"
  }

  @PostMapping("/find-misterx")
  fun postFindMisterx(request: FindMisterxRequest): String {
    val user = userRepository.findByUsername(SecurityContextHolder.getContext().authentication.name)
      .orElseThrow()
    val team = teamRepository.findByMembersContaining(user.id).orElseThrow()
    teamApplicationService.teamFoundMisterx(team.id, request.token)
    return "redirect:/my-team"
  }

  @GetMapping("/my-team")
  fun getMyTeam(model: Model): String {
    val user = userRepository.findByUsername(SecurityContextHolder.getContext().authentication.name)
      .orElseThrow()

    val team = teamRepository.findByMembersContaining(user.id).orElseThrow()
    val misterxList = misterxRepository.findAll()

    val foundList = team.foundMisterx.sortedBy { it.time } .map {
      val misterx = misterxList.find { misterx -> it.misterxId == misterx.id } ?: error("no misterx")
      val misterxUser = userRepository.findById(misterx.userId).orElseThrow()
      FoundMisterxModel(
        misterxUser.firstname,
        misterxUser.lastname,
        it.time.format(DateTimeFormatter.ofPattern("HH:mm"))
      )
    }

    return with(modelService) {
      model.page("my-team") {
        it.addAttribute("foundList", foundList)
      }
    }
  }

  @GetMapping("/leaderboard")
  fun getLeaderboard(model: Model): String {
    val foundCountComparator: Comparator<Team> = Comparator.comparing<Team?, Int?> { it.foundMisterx.count() }.reversed()
    val foundTimeComparator: Comparator<Team> = Comparator.comparing { it.foundMisterx.lastOrNull()?.time ?: LocalDateTime.MIN }
    val compound = foundCountComparator.thenComparing(foundTimeComparator)

    val teams = teamRepository.findAll().sortedWith(compound)

    val leaderboard = teams.mapIndexed { idx, team ->
      LeaderboardEntry(idx + 1, team.name, team.foundMisterx.count(), team.foundMisterx.lastOrNull()?.time?.format(
        DateTimeFormatter.ofPattern("HH:mm")) ?: "---")
    }

    return with(modelService) {
      model.page("leaderboard") {
        it.addAttribute("leaderboardEntries", leaderboard)
      }
    }
  }
}

data class FoundMisterxModel(
  val firstname: String,
  val lastname: String,
  val time: String,
)
