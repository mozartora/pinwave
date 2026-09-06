package com.pinwave.compliance

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry of capabilities, each mapped to the official Pinterest API v5
 * endpoint that powers it — or flagged as not officially available.
 *
 * Pinwave's critical rule (spec §50): if a capability has no documented
 * official endpoint under our current access, we never fall back to scraping.
 * The guard denies it and the UI explains the limitation instead.
 */
enum class PinterestCapability(
    val documentedEndpoint: String?,
    val requiredScopes: List<String>,
) {
    USER_ACCOUNT("GET /v5/user_account", listOf("user_accounts:read")),
    READ_OWN_PINS("GET /v5/pins", listOf("pins:read")),
    READ_OWN_BOARDS("GET /v5/boards", listOf("boards:read")),
    READ_BOARD_PINS("GET /v5/boards/{board_id}/pins", listOf("boards:read", "pins:read")),
    CREATE_PIN("POST /v5/pins", listOf("pins:write")),

    /** Pinterest API v5 exposes no public cross-Pinterest trends endpoint for
     *  general developer access. Kept here so the feature can light up the
     *  moment Pinterest officially ships one. */
    PUBLIC_TRENDS_FEED(documentedEndpoint = null, requiredScopes = emptyList()),

    /** v5 has no general public Pin search across other users' content for
     *  trial/standard access. */
    PUBLIC_PIN_SEARCH(documentedEndpoint = null, requiredScopes = emptyList()),
}

sealed interface ComplianceVerdict {
    data object Allowed : ComplianceVerdict
    data class Denied(val reason: DenyReason) : ComplianceVerdict {
        val userMessage: String
            get() = when (reason) {
                DenyReason.ENDPOINT_NOT_DOCUMENTED ->
                    "This content isn't available through the Pinterest API with the " +
                        "permissions currently available to this application."
                DenyReason.NOT_AUTHENTICATED ->
                    "Connect your Pinterest account to unlock this."
                DenyReason.MISSING_SCOPE ->
                    "Pinterest hasn't granted this app the permission needed for that."
                DenyReason.RATE_LIMITED ->
                    "Pinterest asked us to slow down. Try again in a moment."
            }
    }
}

enum class DenyReason { ENDPOINT_NOT_DOCUMENTED, NOT_AUTHENTICATED, MISSING_SCOPE, RATE_LIMITED }

/**
 * Every sensitive Pinterest operation passes through this guard before it
 * executes (spec §27). The chain is: documented? → authenticated? →
 * scope granted? → rate limit ok?
 */
@Singleton
class PinterestComplianceGuard @Inject constructor() {

    fun check(
        capability: PinterestCapability,
        authenticated: Boolean,
        grantedScopes: Set<String> = emptySet(),
    ): ComplianceVerdict {
        if (capability.documentedEndpoint == null) {
            return ComplianceVerdict.Denied(DenyReason.ENDPOINT_NOT_DOCUMENTED)
        }
        if (!authenticated) {
            return ComplianceVerdict.Denied(DenyReason.NOT_AUTHENTICATED)
        }
        if (!grantedScopes.containsAll(capability.requiredScopes)) {
            return ComplianceVerdict.Denied(DenyReason.MISSING_SCOPE)
        }
        return ComplianceVerdict.Allowed
    }
}
