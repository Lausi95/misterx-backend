package de.lausi95.misterx.backend.application.service

import de.lausi95.misterx.backend.DomainException
import de.lausi95.misterx.backend.application.usecase.RegisterAdminCommand
import de.lausi95.misterx.backend.application.usecase.RegisterAdminUsercase
import de.lausi95.misterx.backend.application.usecase.RegisterUserCommand
import de.lausi95.misterx.backend.application.usecase.RegisterUserUsecase
import de.lausi95.misterx.backend.domain.model.user.User
import de.lausi95.misterx.backend.domain.model.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class UserApplicationService(
  private val userRepository: UserRepository
) :
  RegisterUserUsecase,
  RegisterAdminUsercase {

  @Transactional
  override fun registerUser(command: RegisterUserCommand) {
    if (userRepository.existsByUsername(command.username)) {
      throw DomainException("User with username '${command.username}' already exists.")
    }

    val user = User.create(
      command.username,
      command.firstname,
      command.lastname,
      command.passwordHash
    )

    userRepository.save(user)
  }

  @Transactional
  override fun registerAdmin(command: RegisterAdminCommand) {
    if (userRepository.existsByUsername(command.username)) {
      throw DomainException("User with name ${command.username} alredy exists.")
    }

    val user = User.create(
      command.username,
      command.firstname,
      command.lastname,
      command.passwordHash
    )

    userRepository.save(user)
  }
}