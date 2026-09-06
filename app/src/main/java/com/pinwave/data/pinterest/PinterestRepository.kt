package com.pinwave.data.pinterest

import com.pinwave.compliance.ComplianceVerdict
import com.pinwave.compliance.DenyReason
import com.pinwave.compliance.PinterestCapability
import com.pinwave.compliance.PinterestComplianceGuard
import com.pinwave.data.mock.MockData
import com.pinwave.domain.model.Pin
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of a repository call. [demo] marks data that comes from the local
 * mock set rather than the Pinterest API, so the UI can label it honestly and
 * production builds can refuse to show it as real metrics.
 */
sealed interface RepoResult<out T> {
    data class Ok<T>(val data: T, val demo: Boolean) : RepoResult<T>
    data class Blocked(val userMessage: String) : RepoResult<Nothing>
}

/**
 * Single entry point for Pinterest-derived data (spec §21).
 * UI → use case → this repository → [PinterestApiClient] → official API.
 */
interface PinterestRepository {
    suspend fun trendingNow(): RepoResult<List<Trend>>
    suspend fun trends(category: TrendCategory?): RepoResult<List<Trend>>
    suspend fun trend(id: String): RepoResult<Trend>
    suspend fun searchTrends(query: String): RepoResult<List<Trend>>
    suspend fun pinsForTrend(trendId: String): RepoResult<List<Pin>>
    suspend fun pin(id: String): RepoResult<Pin>
}

/**
 * Mock implementation used while `BuildConfig.USE_MOCK_DATA` is true.
 *
 * It still routes every call through the [PinterestComplianceGuard], exactly
 * like the real implementation will. Since the app is not authenticated and
 * Pinterest exposes no public trends endpoint, trend calls are served from
 * the clearly-flagged demo dataset — the same code path that will serve live
 * data once OAuth + endpoints are wired up.
 */
@Singleton
class MockPinterestRepository @Inject constructor(
    private val guard: PinterestComplianceGuard,
) : PinterestRepository {

    private fun trendsVerdict(): ComplianceVerdict =
        guard.check(PinterestCapability.PUBLIC_TRENDS_FEED, authenticated = false)

    override suspend fun trendingNow(): RepoResult<List<Trend>> =
        when (val verdict = trendsVerdict()) {
            is ComplianceVerdict.Allowed -> RepoResult.Blocked("Unexpected state") // unreachable in mock
            is ComplianceVerdict.Denied ->
                // No official trends endpoint → demo data, honestly labeled.
                RepoResult.Ok(MockData.trends.sortedByDescending { it.growthPercent }, demo = true)
                    .takeIf { verdict.reason == DenyReason.ENDPOINT_NOT_DOCUMENTED }
                    ?: RepoResult.Blocked(verdict.userMessage)
        }

    override suspend fun trends(category: TrendCategory?): RepoResult<List<Trend>> =
        RepoResult.Ok(
            MockData.trends
                .filter { category == null || it.category == category }
                .sortedByDescending { it.growthPercent },
            demo = true,
        )

    override suspend fun trend(id: String): RepoResult<Trend> =
        MockData.trend(id)?.let { RepoResult.Ok(it, demo = true) }
            ?: RepoResult.Blocked("That trend couldn't be found.")

    override suspend fun searchTrends(query: String): RepoResult<List<Trend>> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return RepoResult.Ok(emptyList(), demo = true)
        return RepoResult.Ok(
            MockData.trends.filter {
                it.title.lowercase().contains(q) ||
                    it.category.label.lowercase().contains(q) ||
                    it.themes.any { theme -> theme.lowercase().contains(q) }
            },
            demo = true,
        )
    }

    override suspend fun pinsForTrend(trendId: String): RepoResult<List<Pin>> =
        RepoResult.Ok(MockData.pinsFor(trendId), demo = true)

    override suspend fun pin(id: String): RepoResult<Pin> =
        MockData.pins.firstOrNull { it.id == id }?.let { RepoResult.Ok(it, demo = true) }
            ?: RepoResult.Blocked("That Pin couldn't be found.")
}
