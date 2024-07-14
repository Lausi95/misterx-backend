package de.lausi95.misterx.backend.adapter.security

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.application.service.UserApplicationService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SecurityController(private val userApplicationService: UserApplicationService) {

  @GetMapping("/login")
  fun login(model: Model, @RequestParam(required = false) message: String?): String {
    if (message != null) {
      model.addAttribute("messages", listOf(message))
    }
    return "login"
  }

  @GetMapping("/register")
  fun getRegister(): String {
    return "register"
  }

  @PostMapping("/register")
  fun postRegister(request: RegisterRequest, model: Model): String {
    try {
      if (request.password != request.passwordRepeat) {
        throw DomainException("Passwort und Passwort-Wiederholung stimmen nicht ueberein.")
      }
      userApplicationService.registerUser(request.username, request.firstname, request.lastname, request.password)
      return "redirect:/login?message=Registrierung erfolgreich. Du kannst dich jetzt einloggen."
    } catch (ex: Exception) {
      model.addAttribute("errors", listOf(ex.message))
      return getRegister()
    }
  }
}
