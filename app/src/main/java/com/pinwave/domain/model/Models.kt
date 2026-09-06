package com.pinwave.domain.model

enum class TrendCategory(val label: String) {
    FASHION("Fashion"),
    BEAUTY("Beauty"),
    TRAVEL("Travel"),
    HOME("Home"),
    FOOD("Food"),
    WEDDING("Wedding"),
    FITNESS("Fitness"),
    PHOTOGRAPHY("Photography"),
    ECOMMERCE("Ecommerce"),
    LIFESTYLE("Lifestyle"),
}

enum class TrendStatus(val label: String) {
    RISING("Rising rapidly"),
    STABLE("Holding steady"),
    FALLING("Cooling down"),
    NEW("Just appeared"),
}

/** One sample of a trend's momentum series (e.g. one week). */
data class MomentumPoint(
    val label: String,
    val value: Float,
)

data class Trend(
    val id: String,
    val title: String,
    val category: TrendCategory,
    val growthPercent: Int,
    val status: TrendStatus,
    val momentum: List<MomentumPoint>,
    val imageSeed: String,
    val themes: List<String>,
    val aiWhyRising: String,
    val aiVisualStyle: List<String>,
    val aiAudience: String,
    val contentOpportunities: List<String>,
) {
    val imageUrl: String get() = "https://picsum.photos/seed/$imageSeed/900/1200"
}

data class Pin(
    val id: String,
    val trendId: String,
    val title: String,
    val description: String,
    val sourceName: String,
    val sourceUrl: String,
    val imageSeed: String,
    val aspectRatio: Float, // width / height, for the masonry grid
) {
    val imageUrl: String
        get() = "https://picsum.photos/seed/$imageSeed/600/${(600 / aspectRatio).toInt()}"
}

data class TrendCollection(
    val id: String,
    val name: String,
    val emoji: String,
)

enum class ContentFormat(val label: String) {
    TIKTOK("TikTok"),
    REEL("Instagram Reel"),
    PIN("Pinterest Pin"),
    SHORT("YouTube Short"),
}

enum class ContentStyle(val label: String) {
    EDUCATIONAL("Educational"),
    FUNNY("Funny"),
    LUXURY("Luxury"),
    STORYTELLING("Storytelling"),
    VIRAL("Viral"),
}

data class GeneratedContent(
    val hook: String,
    val concept: String,
    val script: String,
    val caption: String,
    val keywords: List<String>,
)

data class AiAnalysis(
    val visualStyle: List<String>,
    val audience: String,
    val opportunities: List<String>,
)

enum class Plan(val label: String, val price: String) {
    FREE("Free", "$0"),
    PRO("Pro", "$12.99/month"),
    CREATOR("Creator", "$29.99/month"),
}
