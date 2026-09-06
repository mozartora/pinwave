package com.pinwave.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pinwave.domain.model.Plan
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pinwave_prefs")

/** Local user preferences only — no Pinterest data is persisted here. */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val store = context.dataStore

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val PINTEREST_CONNECTED = booleanPreferencesKey("pinterest_connected")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val APPEARANCE = stringPreferencesKey("appearance") // dark | light | system
        val PLAN = stringPreferencesKey("plan")
    }

    val onboardingDone: Flow<Boolean> = store.data.map { it[Keys.ONBOARDING_DONE] ?: false }
    val pinterestConnected: Flow<Boolean> = store.data.map { it[Keys.PINTEREST_CONNECTED] ?: false }
    val notificationsEnabled: Flow<Boolean> = store.data.map { it[Keys.NOTIFICATIONS] ?: true }
    val appearance: Flow<String> = store.data.map { it[Keys.APPEARANCE] ?: "dark" }
    val plan: Flow<Plan> = store.data.map {
        runCatching { Plan.valueOf(it[Keys.PLAN] ?: "FREE") }.getOrDefault(Plan.FREE)
    }

    suspend fun setOnboardingDone() {
        store.edit { it[Keys.ONBOARDING_DONE] = true }
    }

    suspend fun setPinterestConnected(connected: Boolean) {
        store.edit { it[Keys.PINTEREST_CONNECTED] = connected }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        store.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setAppearance(value: String) {
        store.edit { it[Keys.APPEARANCE] = value }
    }

    suspend fun setPlan(plan: Plan) {
        store.edit { it[Keys.PLAN] = plan.name }
    }
}
