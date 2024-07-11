package de.lausi95.misterx.backend.adapter.security

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SecurityController {

  @GetMapping("/login")
  fun login(model: Model): String {
    return "login"
  }
}