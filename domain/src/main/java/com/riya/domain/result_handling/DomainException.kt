package ae.riya.domain.result_handling

sealed class DomainException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    data class NetworkError(
        val code: Int? = null,
        override val message: String = "Network error occurred"
    ) : DomainException(message)

    data class ApiException(
        val code: Int? = null,
        override val message: String = "API error occurred"
    ) : DomainException(message)

    data class SocketTimeoutException(
        override val message: String = "Socket timeout occurred"
    ) : DomainException(message)

    // Represents general unexpected errors
    data class UnknownError(override val message: String = "An unknown error occurred") :
        DomainException(message)
}