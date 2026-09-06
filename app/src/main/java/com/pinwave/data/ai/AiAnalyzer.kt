package com.pinwave.data.ai

import com.pinwave.domain.model.AiAnalysis
import com.pinwave.domain.model.ContentFormat
import com.pinwave.domain.model.ContentStyle
import com.pinwave.domain.model.GeneratedContent
import com.pinwave.domain.model.Pin
import com.pinwave.domain.model.Trend
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

/**
 * AI insight layer. The AI only ever reasons over information the app
 * legitimately has (Pinterest API fields, user context) — it must never
 * invent Pinterest statistics (spec §11).
 */
interface AiAnalyzer {
    suspend fun analyzeTrend(trend: Trend): AiAnalysis
    suspend fun analyzePin(pin: Pin): PinInsight
    suspend fun stealTheTrend(trend: Trend): List<String>
    suspend fun generateContent(
        trend: Trend,
        format: ContentFormat,
        style: ContentStyle,
    ): GeneratedContent
}

data class PinInsight(
    val visualCharacteristics: List<String>,
    val summary: String,
    val contentIdeas: List<String>,
)

/** Demo implementation while no AI backend is wired up. Output is labeled
 *  "AI INSIGHT (demo)" in the UI. */
@Singleton
class MockAiAnalyzer @Inject constructor() : AiAnalyzer {

    override suspend fun analyzeTrend(trend: Trend): AiAnalysis {
        delay(2400) // the staged "Analyzing…" animation plays over this
        return AiAnalysis(
            visualStyle = trend.aiVisualStyle,
            audience = trend.aiAudience,
            opportunities = buildList {
                addAll(trend.contentOpportunities)
                add("Behind-the-scenes: how the ${trend.title.lowercase()} look comes together")
                add("Myth-busting: what people get wrong about ${trend.title.lowercase()}")
            }.take(5),
        )
    }

    override suspend fun analyzePin(pin: Pin): PinInsight {
        delay(1800)
        return PinInsight(
            visualCharacteristics = listOf(
                "Warm, low-contrast palette",
                "Single clear subject with negative space",
                "Texture-forward close framing",
            ),
            summary = "This Pin uses three recurring visual characteristics: a warm " +
                "muted palette, one dominant subject, and generous negative space — " +
                "a combination that tends to earn saves over quick likes.",
            contentIdeas = listOf(
                "Recreate the composition with your own product",
                "Carousel: 3 variations of this exact framing",
                "Short video: the 10-second setup behind this shot",
            ),
        )
    }

    override suspend fun stealTheTrend(trend: Trend): List<String> {
        delay(1500)
        val base = trend.title.lowercase()
        return listOf(
            "\"5 ${trend.title.lowercase()} picks under \$100 that look designer\"",
            "A 30-second transformation video riding the $base aesthetic",
            "\"I tested the $base trend for a week — honest results\"",
            "A beginner's guide carousel: getting the $base look on a budget",
            "POV storytelling: the day $base took over your feed",
        )
    }

    override suspend fun generateContent(
        trend: Trend,
        format: ContentFormat,
        style: ContentStyle,
    ): GeneratedContent {
        delay(2000)
        val name = trend.title
        val hook = when (style) {
            ContentStyle.EDUCATIONAL -> "Nobody explains $name properly. Here's the 30-second version."
            ContentStyle.FUNNY -> "POV: $name just took over your entire feed and you don't know why."
            ContentStyle.LUXURY -> "$name, but make it quiet luxury."
            ContentStyle.STORYTELLING -> "Three weeks ago I ignored the $name trend. Big mistake."
            ContentStyle.VIRAL -> "$name is about to be everywhere. You heard it here first."
        }
        return GeneratedContent(
            hook = hook,
            concept = "A ${format.label.lowercase()} built around one strong visual of " +
                "$name. Open on the hook, show the look in the first two seconds, then " +
                "deliver one useful takeaway before the loop point.",
            script = "0–2s: Hook — \"$hook\"\n" +
                "2–8s: Show the strongest visual of the trend, no talking.\n" +
                "8–20s: One practical takeaway the viewer can copy today.\n" +
                "20–25s: Recap in one line + soft CTA to save for later.",
            caption = "$name — save this before it blows up. #${trend.category.label.lowercase()}",
            keywords = buildList {
                add(name.lowercase())
                add("${trend.category.label.lowercase()} trends")
                addAll(trend.themes.map { it.lowercase() })
            },
        )
    }
}
