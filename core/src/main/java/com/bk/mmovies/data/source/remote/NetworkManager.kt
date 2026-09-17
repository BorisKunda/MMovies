package com.bk.mmovies.data.source.remote

import com.bk.mmovies.data.source.remote.dto.ErrorDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.NetworkError
import com.bk.mmovies.util.logError
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class NetworkManager @Inject constructor(private val gson: Gson) {

    suspend fun <T> executeApiCall(
            callName: String,
            apiCall: suspend () -> Response<T>
                                  ): ApiCallResult<T> {
        try {
            val response = apiCall()
            val responseBody = response.body()

            if (response.isSuccessful) {
                if (responseBody != null) {
                    return ApiCallResult.Success(responseBody)
                } else {
                    logError(
                            callName,
                            "Failure: empty response"
                            )
                    return ApiCallResult.Failure(NetworkError.EmptyBody)
                }
            } else {
                val errorBody: ResponseBody? = response.errorBody()
                var errorDto: ErrorDto? = null

                if (errorBody != null) {
                    var errorBodyText: String? = null
                    try {
                        errorBodyText = errorBody.string()
                        errorDto = gson.fromJson(
                                errorBodyText,
                                ErrorDto::class.java
                                                )
                        logError(
                                callName,
                                "Failure: network error - code:${response.code()} " +
                                "message: ${response.message()}" +
                                "api error code: ${errorDto.statusCode} api error message ${errorDto.message}"
                                )
                    } catch (exception: Exception) {
                        logError(
                                callName,
                                "Failure: network error - code:${response.code()} " +
                                "message: ${response.message()} - failed to parse error body exception: ${exception.message ?: "unknown"} raw error response: $errorBodyText"
                                )
                    }
                }
                return ApiCallResult.Failure(
                        NetworkError.HttpError(
                                response.code(),
                                response.message(),
                                errorDto
                                              )
                                            )
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (e: Exception) {
            logError(
                    callName,
                    "Failure: exception ${e.message ?: "unknown"}"
                    )
            return ApiCallResult.Failure(NetworkError.ExceptionError(e))
        }
    }
}