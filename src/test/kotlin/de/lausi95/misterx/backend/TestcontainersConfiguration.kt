package de.lausi95.misterx.backend

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@Configuration(proxyBeanMethods = false)
class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  fun postgresContainer(): PostgreSQLContainer<*> {
    return PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
  }
}
