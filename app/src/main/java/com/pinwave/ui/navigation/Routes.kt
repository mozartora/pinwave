package com.pinwave.ui.navigation

object Routes {
    const val ONBOARDING = "onboarding"

    // Bottom-nav destinations (§29)
    const val HOME = "home"
    const val RADAR = "radar"
    const val EXPLORE = "explore"
    const val SAVED = "saved"

    const val SEARCH = "search"
    const val DROP = "todays_drop"
    const val SETTINGS = "settings"
    const val PAYWALL = "paywall"

    const val TREND = "trend/{trendId}"
    const val PINS = "pins/{trendId}"
    const val PIN = "pin/{pinId}"
    const val STEAL = "steal/{trendId}"
    const val CONTENT = "content/{trendId}"
    const val LEGAL = "legal/{page}"

    fun trend(id: String) = "trend/$id"
    fun pins(trendId: String) = "pins/$trendId"
    fun pin(id: String) = "pin/$id"
    fun steal(trendId: String) = "steal/$trendId"
    fun content(trendId: String) = "content/$trendId"
    fun legal(page: String) = "legal/$page"
    fun explore(category: String? = null) =
        if (category == null) EXPLORE else "$EXPLORE?category=$category"

    const val EXPLORE_WITH_ARG = "explore?category={category}"
}
