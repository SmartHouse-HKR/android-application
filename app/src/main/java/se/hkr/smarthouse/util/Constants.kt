package se.hkr.smarthouse.util

class Constants {
    companion object {
        const val WEB_API_BASE_URL = "https://open-api.xyz/api/"
        const val NETWORK_TIMEOUT = 2000L
        const val TESTING_NETWORK_DELAY = 1L // Fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // Fake cache delay for testing
        const val LOGIN_EMAIL = "fast@fast.se"
        const val LOGIN_PASS = "fast"

        const val BASE_TOPIC = "Smarthome"
        const val QOS = 2 // We always must make sure what we send is received to improve UX
    }
}