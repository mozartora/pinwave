package com.pinwave.data.pinterest.auth

import com.pinwave.data.prefs.UserPreferences
import com.pinwave.security.TokenStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Pinterest OAuth orchestration (roadmap step 5).
 *
 * Target flow per spec §22:
 *   app → Pinterest authorization page (PKCE + state)
 *       → authorization code
 *       → Pinwave backend exchanges code (client secret lives only there)
 *       → encrypted token storage via [TokenStore].
 *
 * Until the backend exists, this manager only models the connected/disconnected
 * state locally so onboarding and settings can be built and tested.
 */
@Singleton
class PinterestAuthManager @Inject constructor(
    private val tokenStore: TokenStore,
    private val prefs: UserPreferences,
) {
    val isConnected: Flow<Boolean> = prefs.pinterestConnected

    /** Placeholder for the Custom-Tab OAuth launch; not functional in demo. */
    suspend fun markConnectedDemo() = prefs.setPinterestConnected(true)

    suspend fun disconnect() {
        tokenStore.clear()
        prefs.setPinterestConnected(false)
    }
}
