package com.pinwave.data.pinterest

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Official Pinterest API v5 client (spec §21).
 *
 * Only endpoints confirmed in Pinterest's developer documentation belong here.
 * Nothing in this file scrapes, automates a browser, or calls undocumented
 * paths — and every call is gated by PinterestComplianceGuard upstream.
 *
 * Unused while BuildConfig.USE_MOCK_DATA is true; wired up in roadmap step 6.
 */
interface PinterestApiClient {

    /** GET /v5/user_account — profile of the authorized account. */
    @GET("v5/user_account")
    suspend fun userAccount(): PinterestUserDto

    /** GET /v5/pins — Pins owned by the authorized account. */
    @GET("v5/pins")
    suspend fun ownPins(@Query("page_size") pageSize: Int = 25): PagedResponse<PinDto>

    /** GET /v5/boards — boards owned by the authorized account. */
    @GET("v5/boards")
    suspend fun ownBoards(@Query("page_size") pageSize: Int = 25): PagedResponse<BoardDto>

    /** GET /v5/boards/{board_id}/pins — Pins on one of the user's boards. */
    @GET("v5/boards/{board_id}/pins")
    suspend fun boardPins(
        @Path("board_id") boardId: String,
        @Query("page_size") pageSize: Int = 25,
    ): PagedResponse<PinDto>

    companion object {
        const val BASE_URL = "https://api.pinterest.com/"

        fun create(client: OkHttpClient): PinterestApiClient {
            val json = Json { ignoreUnknownKeys = true }
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(PinterestApiClient::class.java)
        }

        fun defaultHttpClient(): OkHttpClient {
            // Log redaction matters: Authorization headers carry tokens and
            // must never reach logs (spec §23).
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
                redactHeader("Authorization")
            }
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }
    }
}
