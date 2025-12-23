package side.flab.goforawalk.app.support.error

class BadRequestException : RuntimeException {
  constructor(message: String? = null) : super(message)

  constructor(message: String, cause: Throwable) : super(message, cause)
}