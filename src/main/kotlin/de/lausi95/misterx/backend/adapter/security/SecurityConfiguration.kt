package de.lausi95.misterx.backend.adapter.security

import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Component

@Component
class MisterxUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

  private final val log = LoggerFactory.getLogger("login")

  override fun loadUserByUsername(username: String?): UserDetails {
    log.info("logging in $username")

    if (username == null) {
      error("username is null")
    }

    val user = userRepository.findByUsername(username).orElseThrow()
    return User(username, user.passwordHash, emptyList())
  }
}

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

  @Bean
  fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun filterChain(
    http: HttpSecurity,
    misterxUserDetailsService: MisterxUserDetailsService
  ): SecurityFilterChain {
    return http
      .authorizeHttpRequests {
        it.requestMatchers("*.css").permitAll()
          .requestMatchers("*.js").permitAll()
          .requestMatchers("/").permitAll()
          .requestMatchers("/api/map").permitAll()
          .requestMatchers("/api/locations").permitAll()
          .requestMatchers("/app").permitAll()
          .requestMatchers("/leaderboard").permitAll()
          .requestMatchers("/login").permitAll()
          .requestMatchers("/register").permitAll()
          .anyRequest().authenticated()
      }
      .formLogin {
        it.loginPage("/login")
          .defaultSuccessUrl("/", true)
          .failureUrl("/login?failure")
          .permitAll()
      }
      .logout {
        it.logoutSuccessUrl("/")
      }
      .httpBasic {}
      .csrf { it.disable() }
      .build()
  }
}