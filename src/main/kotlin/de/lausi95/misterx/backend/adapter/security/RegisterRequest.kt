package de.lausi95.misterx.backend.adapter.security

data class RegisterRequest(
  val username: String,
  val firstname: String,
  val lastname: String,
  val password: String,
  val passwordRepeat: String,
)
