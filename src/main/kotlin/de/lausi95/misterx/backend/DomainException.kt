package de.lausi95.misterx.backend

import java.lang.Exception

class DomainException: Exception {
  constructor(message: String?) : super(message)
  constructor(cause: Throwable?) : super(cause)
}