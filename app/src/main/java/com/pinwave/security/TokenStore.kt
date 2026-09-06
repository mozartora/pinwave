package com.pinwave.security

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Token storage boundary (spec §22/§23).
 *
 * The Pinterest client secret never ships in the app — the authorization-code
 * exchange happens on the Pinwave backend. What the app stores locally is
 * only the resulting access/refresh pair, and the production implementation
 * must back this with encrypted storage (Tink / EncryptedSharedPreferences).
 *
 * Tokens are never logged.
 */
interface TokenStore {
    val hasSession: StateFlow<Boolean>
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun accessToken(): String?
    suspend fun clear()
}

/** In-memory stand-in until OAuth (roadmap step 5) lands. */
@Singleton
class SessionTokenStore @Inject constructor() : TokenStore {
    override val hasSession = MutableStateFlow(false)
    private var access: String? = null
    private var refresh: String? = null

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        access = accessToken
        refresh = refreshToken
        hasSession.value = true
    }

    override suspend fun accessToken(): String? = access

    override suspend fun clear() {
        access = null
        refresh = null
        hasSession.value = false
    }
}
