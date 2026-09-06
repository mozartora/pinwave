package com.pinwave.domain.usecase

import com.pinwave.data.ai.AiAnalyzer
import com.pinwave.data.ai.PinInsight
import com.pinwave.data.pinterest.PinterestRepository
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.data.repository.SavedRepository
import com.pinwave.domain.model.AiAnalysis
import com.pinwave.domain.model.ContentFormat
import com.pinwave.domain.model.ContentStyle
import com.pinwave.domain.model.GeneratedContent
import com.pinwave.domain.model.Pin
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCategory
import javax.inject.Inject

class GetTrendingNowUseCase @Inject constructor(private val repo: PinterestRepository) {
    suspend operator fun invoke(): RepoResult<List<Trend>> = repo.trendingNow()
}

class GetTrendsUseCase @Inject constructor(private val repo: PinterestRepository) {
    suspend operator fun invoke(category: TrendCategory?): RepoResult<List<Trend>> =
        repo.trends(category)
}

class GetTrendUseCase @Inject constructor(private val repo: PinterestRepository) {
    suspend operator fun invoke(id: String): RepoResult<Trend> = repo.trend(id)
}

class SearchTrendsUseCase @Inject constructor(private val repo: PinterestRepository) {
    suspend operator fun invoke(query: String): RepoResult<List<Trend>> =
        repo.searchTrends(query)
}

class GetPinsForTrendUseCase @Inject constructor(private val repo: PinterestRepository) {
    suspend operator fun invoke(trendId: String): RepoResult<List<Pin>> =
        repo.pinsForTrend(trendId)
}

class GetPinUseCase @Inject constructor(private val repo: PinterestRepository) {
    suspend operator fun invoke(id: String): RepoResult<Pin> = repo.pin(id)
}

class AnalyzeTrendUseCase @Inject constructor(private val ai: AiAnalyzer) {
    suspend operator fun invoke(trend: Trend): AiAnalysis = ai.analyzeTrend(trend)
}

class AnalyzePinUseCase @Inject constructor(private val ai: AiAnalyzer) {
    suspend operator fun invoke(pin: Pin): PinInsight = ai.analyzePin(pin)
}

class StealTheTrendUseCase @Inject constructor(private val ai: AiAnalyzer) {
    suspend operator fun invoke(trend: Trend): List<String> = ai.stealTheTrend(trend)
}

class GenerateContentUseCase @Inject constructor(private val ai: AiAnalyzer) {
    suspend operator fun invoke(
        trend: Trend,
        format: ContentFormat,
        style: ContentStyle,
    ): GeneratedContent = ai.generateContent(trend, format, style)
}

class ToggleSaveTrendUseCase @Inject constructor(private val saved: SavedRepository) {
    suspend operator fun invoke(trendId: String, currentlySaved: Boolean) =
        saved.toggleSaved(trendId, currentlySaved)
}
