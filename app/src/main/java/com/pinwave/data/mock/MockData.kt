package com.pinwave.data.mock

import com.pinwave.domain.model.MomentumPoint
import com.pinwave.domain.model.Pin
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCategory
import com.pinwave.domain.model.TrendStatus

/**
 * DEMO DATA — used while `BuildConfig.USE_MOCK_DATA` is true.
 *
 * None of these numbers come from Pinterest. They exist so the UI can be
 * built and explored before the official Pinterest API integration (roadmap
 * steps 4–6) lands. They must never be presented to users as real Pinterest
 * metrics in a production build.
 */
object MockData {

    private fun series(vararg values: Number): List<MomentumPoint> {
        val weeks = listOf("W1", "W2", "W3", "W4", "W5", "W6", "W7", "W8", "W9", "W10", "W11", "W12")
        return values.mapIndexed { i, v -> MomentumPoint(weeks[i], v.toFloat()) }
    }

    val trends: List<Trend> = listOf(
        Trend(
            id = "brown-suede",
            title = "Brown Suede Bags",
            category = TrendCategory.FASHION,
            growthPercent = 83,
            status = TrendStatus.RISING,
            momentum = series(12, 14, 15, 18, 22, 27, 33, 41, 52, 64, 76, 88),
            imageSeed = "pinwave-suede",
            themes = listOf("Vintage", "Minimal", "70s"),
            aiWhyRising = "Brown suede is appearing increasingly alongside vintage " +
                "silhouettes and minimalist outfits. The texture photographs warmly, " +
                "which makes it perform well in saves and close-ups.",
            aiVisualStyle = listOf("Warm neutrals", "Soft daylight", "Vintage grain"),
            aiAudience = "Women 22–38 into quiet-luxury and vintage-inspired styling.",
            contentOpportunities = listOf(
                "5 brown bags under $100 that look designer",
                "Styling suede through three seasons",
                "Suede care routine nobody tells you about",
            ),
        ),
        Trend(
            id = "japandi-bedroom",
            title = "Japandi Bedroom",
            category = TrendCategory.HOME,
            growthPercent = 67,
            status = TrendStatus.RISING,
            momentum = series(20, 22, 24, 26, 30, 34, 39, 44, 50, 57, 63, 70),
            imageSeed = "pinwave-japandi",
            themes = listOf("Minimal", "Natural wood", "Calm"),
            aiWhyRising = "Japandi keeps merging Japanese restraint with Scandinavian " +
                "warmth. Low beds, linen bedding and pale wood dominate the saves, " +
                "reflecting a broader shift toward calmer bedrooms.",
            aiVisualStyle = listOf("Muted palette", "Negative space", "Natural texture"),
            aiAudience = "Home improvers 25–45 planning slow, budget-conscious makeovers.",
            contentOpportunities = listOf(
                "Japandi bedroom on a $500 budget",
                "5 rules of Japandi most people miss",
                "Room tour: before/after Japandi refresh",
            ),
        ),
        Trend(
            id = "cherry-red",
            title = "Cherry Red Accents",
            category = TrendCategory.FASHION,
            growthPercent = 52,
            status = TrendStatus.RISING,
            momentum = series(25, 24, 26, 29, 31, 35, 38, 42, 46, 50, 53, 57),
            imageSeed = "pinwave-cherry",
            themes = listOf("Bold accent", "Streetwear", "Retro"),
            aiWhyRising = "A single cherry-red piece — a bag, shoe or lip — is being " +
                "used to lift otherwise neutral outfits. Low commitment, high contrast, " +
                "very saveable.",
            aiVisualStyle = listOf("High contrast", "Neutral base + one pop"),
            aiAudience = "Trend-aware dressers 18–30 who remix basics.",
            contentOpportunities = listOf(
                "One red item, five outfits",
                "The cherry-red rule for capsule wardrobes",
            ),
        ),
        Trend(
            id = "mediterranean-travel",
            title = "Mediterranean Summer",
            category = TrendCategory.TRAVEL,
            growthPercent = 44,
            status = TrendStatus.RISING,
            momentum = series(18, 19, 21, 22, 25, 27, 30, 33, 36, 39, 42, 45),
            imageSeed = "pinwave-med",
            themes = listOf("Coastal", "Slow travel", "Golden hour"),
            aiWhyRising = "Itineraries are shifting from checklist tourism to slow " +
                "coastal stays — whitewashed towns, long lunches, swimmable coves. " +
                "Saves cluster around lesser-known islands.",
            aiVisualStyle = listOf("Golden hour", "White + azure", "Film grain"),
            aiAudience = "Couples 25–40 planning shoulder-season Europe trips.",
            contentOpportunities = listOf(
                "5 quiet Mediterranean islands for September",
                "Packing list: 10 days, one carry-on",
            ),
        ),
        Trend(
            id = "protein-desserts",
            title = "Protein Desserts",
            category = TrendCategory.FOOD,
            growthPercent = 38,
            status = TrendStatus.RISING,
            momentum = series(15, 16, 18, 19, 22, 24, 26, 29, 31, 34, 36, 39),
            imageSeed = "pinwave-protein",
            themes = listOf("High protein", "Guilt-free", "Meal prep"),
            aiWhyRising = "Dessert formats are being reworked around protein targets. " +
                "Recipes with fewer than five ingredients spread fastest, especially " +
                "no-bake ones.",
            aiVisualStyle = listOf("Overhead shots", "Drizzle close-ups"),
            aiAudience = "Gym-goers 20–35 who track macros but want dessert.",
            contentOpportunities = listOf(
                "3-ingredient protein brownie test",
                "Rating viral protein desserts honestly",
            ),
        ),
        Trend(
            id = "streetwear-layering",
            title = "Streetwear Layering",
            category = TrendCategory.FASHION,
            growthPercent = 29,
            status = TrendStatus.STABLE,
            momentum = series(30, 31, 30, 32, 33, 32, 34, 35, 34, 36, 37, 38),
            imageSeed = "pinwave-street",
            themes = listOf("Oversized", "Utility", "Monochrome"),
            aiWhyRising = "Layering content peaks every shoulder season. Boxy outerwear " +
                "over longline tees is the current dominant silhouette.",
            aiVisualStyle = listOf("Monochrome", "Urban backdrops"),
            aiAudience = "Menswear-curious 18–28.",
            contentOpportunities = listOf(
                "Layering formula: 3 pieces, 7 outfits",
            ),
        ),
        Trend(
            id = "glass-skin",
            title = "Glass Skin Routine",
            category = TrendCategory.BEAUTY,
            growthPercent = 24,
            status = TrendStatus.STABLE,
            momentum = series(35, 34, 36, 35, 37, 38, 37, 39, 40, 39, 41, 42),
            imageSeed = "pinwave-glass",
            themes = listOf("Dewy", "Minimal routine", "K-beauty"),
            aiWhyRising = "The look is shifting from 10-step routines to barrier-first " +
                "minimalism. Fewer products, more technique.",
            aiVisualStyle = listOf("Dewy close-ups", "Bathroom-shelf flatlays"),
            aiAudience = "Skincare enthusiasts 18–34.",
            contentOpportunities = listOf(
                "Glass skin with only 3 products",
            ),
        ),
        Trend(
            id = "film-photography",
            title = "Film Photography Aesthetic",
            category = TrendCategory.PHOTOGRAPHY,
            growthPercent = 41,
            status = TrendStatus.RISING,
            momentum = series(10, 12, 13, 15, 17, 20, 23, 26, 30, 33, 37, 41),
            imageSeed = "pinwave-film",
            themes = listOf("Grain", "Light leaks", "Candid"),
            aiWhyRising = "Digital photos edited to look like film — or shot on actual " +
                "35mm — keep outperforming clean digital looks in saves. Imperfection " +
                "reads as authenticity.",
            aiVisualStyle = listOf("Visible grain", "Warm casts", "Flash at night"),
            aiAudience = "Creators 18–30 building a recognizable feed style.",
            contentOpportunities = listOf(
                "Free Lightroom film recipe",
                "Shooting a whole day on one roll",
            ),
        ),
        Trend(
            id = "matcha-everything",
            title = "Matcha Everything",
            category = TrendCategory.FOOD,
            growthPercent = 33,
            status = TrendStatus.RISING,
            momentum = series(22, 23, 24, 26, 27, 29, 30, 32, 33, 35, 36, 38),
            imageSeed = "pinwave-matcha",
            themes = listOf("Café culture", "Green", "Ritual"),
            aiWhyRising = "Matcha has moved from lattes into baking, breakfast and " +
                "desserts. The color alone drives stops in the feed.",
            aiVisualStyle = listOf("Green on ceramic", "Pour shots"),
            aiAudience = "Café-goers and home baristas 20–35.",
            contentOpportunities = listOf(
                "Ceremonial vs culinary matcha explained",
            ),
        ),
        Trend(
            id = "micro-wedding",
            title = "Micro Weddings",
            category = TrendCategory.WEDDING,
            growthPercent = 19,
            status = TrendStatus.STABLE,
            momentum = series(28, 28, 29, 30, 30, 31, 31, 32, 33, 33, 34, 34),
            imageSeed = "pinwave-wedding",
            themes = listOf("Intimate", "Backyard", "Editorial"),
            aiWhyRising = "Couples keep trading guest count for experience quality. " +
                "Long-table dinners and editorial photography define the look.",
            aiVisualStyle = listOf("Candlelight", "Long tables", "Documentary style"),
            aiAudience = "Engaged couples 26–36 planning under 40 guests.",
            contentOpportunities = listOf(
                "Micro wedding budget breakdown",
            ),
        ),
        Trend(
            id = "pilates-era",
            title = "Pilates Era",
            category = TrendCategory.FITNESS,
            growthPercent = 47,
            status = TrendStatus.RISING,
            momentum = series(16, 18, 20, 22, 25, 28, 31, 34, 38, 42, 45, 49),
            imageSeed = "pinwave-pilates",
            themes = listOf("Reformer", "Soft strength", "Routine"),
            aiWhyRising = "Low-impact strength training keeps gaining share from HIIT. " +
                "The aesthetic — matching sets, studios, morning routines — is as " +
                "saveable as the workouts.",
            aiVisualStyle = listOf("Studio light", "Matching sets", "Slow motion"),
            aiAudience = "Women 22–40 building sustainable routines.",
            contentOpportunities = listOf(
                "Beginner reformer week: honest diary",
            ),
        ),
        Trend(
            id = "vintage-typewriters",
            title = "Analog Desk Setup",
            category = TrendCategory.HOME,
            growthPercent = -12,
            status = TrendStatus.FALLING,
            momentum = series(40, 39, 38, 36, 35, 33, 32, 30, 29, 28, 26, 25),
            imageSeed = "pinwave-analog",
            themes = listOf("Analog", "Desk aesthetic", "Slow living"),
            aiWhyRising = "After a strong run, desk-aesthetic saves are rotating toward " +
                "warmer, more personal spaces. The look is maturing rather than dying.",
            aiVisualStyle = listOf("Moody light", "Wood + brass"),
            aiAudience = "WFH professionals 25–40.",
            contentOpportunities = listOf(
                "Analog desk tour: what survived a year",
            ),
        ),
        Trend(
            id = "chrome-nails",
            title = "Chrome Nails",
            category = TrendCategory.BEAUTY,
            growthPercent = 58,
            status = TrendStatus.RISING,
            momentum = series(14, 15, 17, 19, 22, 26, 30, 35, 41, 47, 53, 60),
            imageSeed = "pinwave-chrome",
            themes = listOf("Metallic", "Glazed", "Y2K"),
            aiWhyRising = "Chrome finishes are cycling back with softer, pearl-adjacent " +
                "tones. Short almond shapes dominate over long stiletto sets this time.",
            aiVisualStyle = listOf("Macro shots", "Reflective surfaces"),
            aiAudience = "Nail-content viewers 16–30.",
            contentOpportunities = listOf(
                "Chrome powder at home vs salon",
            ),
        ),
        Trend(
            id = "digital-nomad-setup",
            title = "Nomad Work Setup",
            category = TrendCategory.LIFESTYLE,
            growthPercent = 8,
            status = TrendStatus.NEW,
            momentum = series(10, 11, 10, 12, 12, 13, 13, 14, 14, 15, 15, 16),
            imageSeed = "pinwave-nomad",
            themes = listOf("Remote work", "Travel", "Minimal carry"),
            aiWhyRising = "Portable setups are consolidating around fewer, better items. " +
                "Content shifts from gear hauls to one-bag philosophies.",
            aiVisualStyle = listOf("Café desks", "Window light"),
            aiAudience = "Remote workers 24–38.",
            contentOpportunities = listOf(
                "My entire office fits in this pouch",
            ),
        ),
    )

    val pins: List<Pin> = listOf(
        Pin("pin-01", "brown-suede", "The suede edit", "Slouchy brown suede tote styled with cream knit and vintage denim.", "Pinterest · @atelier.nord", "https://www.pinterest.com", "pp-pin-01", 0.72f),
        Pin("pin-02", "brown-suede", "70s shoulder bag", "Cognac suede shoulder bag with brass hardware, shot in soft daylight.", "Pinterest · @retrovault", "https://www.pinterest.com", "pp-pin-02", 0.66f),
        Pin("pin-03", "brown-suede", "Minimal carry", "One bag, one coat, one tone. Autumn capsule in warm neutrals.", "Pinterest · @quietlux", "https://www.pinterest.com", "pp-pin-03", 0.8f),
        Pin("pin-04", "brown-suede", "Suede care 101", "Brush, spray, steam — keeping suede alive through winter.", "Pinterest · @caremanual", "https://www.pinterest.com", "pp-pin-04", 0.7f),
        Pin("pin-05", "japandi-bedroom", "Low oak bed", "Japandi bedroom with low oak frame, linen bedding, paper lamp.", "Pinterest · @studio.muji", "https://www.pinterest.com", "pp-pin-05", 0.75f),
        Pin("pin-06", "japandi-bedroom", "Calm corners", "Reading nook: floor cushion, bonsai, warm 2700K light.", "Pinterest · @slowspaces", "https://www.pinterest.com", "pp-pin-06", 0.68f),
        Pin("pin-07", "japandi-bedroom", "Before / after", "Builder-grade bedroom to Japandi calm for under $500.", "Pinterest · @budgetdwell", "https://www.pinterest.com", "pp-pin-07", 0.78f),
        Pin("pin-08", "cherry-red", "The red rule", "Grey wool coat, cherry shoulder bag. That's the whole outfit.", "Pinterest · @colorstudy", "https://www.pinterest.com", "pp-pin-08", 0.72f),
        Pin("pin-09", "cherry-red", "Red lip, red sole", "Matching micro-accents: lipstick, bag strap, loafer trim.", "Pinterest · @detailshot", "https://www.pinterest.com", "pp-pin-09", 0.64f),
        Pin("pin-10", "mediterranean-travel", "Symi harbor", "Pastel neoclassical houses around a tiny harbor, golden hour.", "Pinterest · @slowtravel", "https://www.pinterest.com", "pp-pin-10", 0.7f),
        Pin("pin-11", "mediterranean-travel", "One carry-on", "Ten days in the Cyclades with a 35L bag. Full list inside.", "Pinterest · @packlight", "https://www.pinterest.com", "pp-pin-11", 0.82f),
        Pin("pin-12", "mediterranean-travel", "Long lunch", "Seaside taverna table: grilled fish, lemon, linen napkin.", "Pinterest · @tablesofgreece", "https://www.pinterest.com", "pp-pin-12", 0.74f),
        Pin("pin-13", "protein-desserts", "3-ingredient brownie", "Greek yogurt, cocoa, protein powder. 22g protein per square.", "Pinterest · @macrokitchen", "https://www.pinterest.com", "pp-pin-13", 0.76f),
        Pin("pin-14", "protein-desserts", "Frozen bark", "Yogurt bark with berries and dark chocolate drizzle.", "Pinterest · @sweetmacros", "https://www.pinterest.com", "pp-pin-14", 0.69f),
        Pin("pin-15", "film-photography", "Portra tones", "Golden hour portrait on Portra 400, visible grain, soft skin.", "Pinterest · @graindiary", "https://www.pinterest.com", "pp-pin-15", 0.67f),
        Pin("pin-16", "film-photography", "Night flash", "Direct flash at night, hard shadows, Y2K energy.", "Pinterest · @pointnshoot", "https://www.pinterest.com", "pp-pin-16", 0.71f),
        Pin("pin-17", "pilates-era", "Reformer morning", "Sunlit studio, matching set, first class of the day.", "Pinterest · @softstrength", "https://www.pinterest.com", "pp-pin-17", 0.73f),
        Pin("pin-18", "pilates-era", "Home corner", "Foldable reformer in a small apartment, styled simply.", "Pinterest · @homestretch", "https://www.pinterest.com", "pp-pin-18", 0.79f),
        Pin("pin-19", "chrome-nails", "Pearl chrome", "Short almond nails with soft pearl chrome finish, macro shot.", "Pinterest · @nailarchive", "https://www.pinterest.com", "pp-pin-19", 0.7f),
        Pin("pin-20", "matcha-everything", "Iced matcha pour", "Ceremonial grade over oat milk, slow pour, green gradient.", "Pinterest · @whiskdaily", "https://www.pinterest.com", "pp-pin-20", 0.68f),
        Pin("pin-21", "matcha-everything", "Matcha basque", "Burnt basque cheesecake with a matcha swirl.", "Pinterest · @bakegreen", "https://www.pinterest.com", "pp-pin-21", 0.77f),
        Pin("pin-22", "streetwear-layering", "Boxy layers", "Cropped work jacket over longline tee, monochrome palette.", "Pinterest · @layeredform", "https://www.pinterest.com", "pp-pin-22", 0.72f),
        Pin("pin-23", "glass-skin", "Three-step glass", "Essence, moisturizer, SPF. Dewy finish in natural light.", "Pinterest · @barrierfirst", "https://www.pinterest.com", "pp-pin-23", 0.75f),
        Pin("pin-24", "micro-wedding", "Table for twelve", "Candlelit long table in a backyard, editorial photography.", "Pinterest · @smallvows", "https://www.pinterest.com", "pp-pin-24", 0.66f),
    )

    val dailyDropIds: List<String> = listOf(
        "brown-suede",
        "japandi-bedroom",
        "cherry-red",
        "mediterranean-travel",
        "protein-desserts",
    )

    fun trend(id: String): Trend? = trends.firstOrNull { it.id == id }

    fun pinsFor(trendId: String): List<Pin> = pins.filter { it.trendId == trendId }
}
