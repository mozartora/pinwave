package com.pinwave.data.billing

import com.pinwave.data.prefs.UserPreferences
import com.pinwave.domain.model.Plan
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * Billing boundary. The production implementation will use Google Play
 * Billing (spec §33); this demo implementation only flips a local preference.
 */
interface BillingRepository {
    val currentPlan: Flow<Plan>
    suspend fun subscribe(plan: Plan)
    suspend fun restore()
}

@Singleton
class MockBillingRepository @Inject constructor(
    private val prefs: UserPreferences,
) : BillingRepository {
    override val currentPlan: Flow<Plan> = prefs.plan

    override suspend fun subscribe(plan: Plan) {
        delay(1200) // stand-in for the Play Billing sheet
        prefs.setPlan(plan)
    }

    override suspend fun restore() = prefs.setPlan(Plan.FREE)
}
