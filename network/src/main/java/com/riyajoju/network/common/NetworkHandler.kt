package com.riyajoju.network.common

import com.riya.domain.result_handling.DomainException
import com.riya.domain.result_handling.Result
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkHandler {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(
                        DomainException.UnknownError(
                            message = "Response body is null"
                        )
                    )
                }
            } else {
                Result.Error(
                    DomainException.ApiException(
                        code = response.code(),
                        message = response.message().ifBlank { "API error occurred" }
                    )
                )
            }
        } catch (e: SocketTimeoutException) {
            Result.Error(
                DomainException.SocketTimeoutException(
                    message = "Request timed out: ${e.message ?: "Please try again"}"
                )
            )
        } catch (e: UnknownHostException) {
            Result.Error(
                DomainException.NetworkError(
                    message = "Unable to reach server. Check your internet connection."
                )
            )
        } catch (e: IOException) {
            Result.Error(
                DomainException.NetworkError(
                    message = "Network error: ${e.message ?: "Please check your connection"}"
                )
            )
        } catch (e: Exception) {
            Result.Error(
                DomainException.UnknownError(
                    message = e.message ?: "An unexpected error occurred"
                )
            )
        }
    }
}