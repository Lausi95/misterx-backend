package de.lausi95.misterx.backend.adapter.web

import de.lausi95.misterx.backend.domain.model.team.TeamRepository
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.ui.Model

@Component
class ModelService(
  private val userRepository: UserRepository,
  private val teamRepository: TeamRepository
) {

  fun Model.page(page: String, modelFn: (model: Model) -> Unit): String {
    val username = SecurityContextHolder.getContext().authentication.name
    if (username != "anonymousUser") {
      val user = userRepository.findByUsername(username).orElse(null)

      addAttribute("username", user.username)
      addAttribute("admin", user.admin)
      addAttribute("teamname", teamRepository.findByMembersContaining(user.id).map { it.name }.orElse(null))
    }

    modelFn(this)
    addAttribute("currentPage", page)
    addAttribute("view", "views/$page")
    return "page"
  }
}
