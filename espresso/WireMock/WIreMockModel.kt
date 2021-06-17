import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// This model can be used to structure your requests/responses

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

@JsonClass(generateAdapter = true)
data class WireMockMapping(
    val scenarioName: String? = "Default Scenario",
    val requiredScenarioState: String? = "Started",
    val newScenarioState: String? = "Started",
    val priority: String? = "1",
    val request: WireMockMappingRequest,
    val response: WireMockMappingResponse
)

@JsonClass(generateAdapter = true)
data class WireMockMappingRequest(
    val method: String? = "GET",
    val url: String? = null,
    val urlPattern: String? = null,
    val urlPath: String? = null,
    val urlPathPattern: String? = null,
    val queryParameters: QueryParameters? = null
)

@JsonClass(generateAdapter = true)
data class QueryParameters(
    val userGuid: QueryObject? = null
)

@JsonClass(generateAdapter = true)
data class QueryObject(
    val matches: String
)

@JsonClass(generateAdapter = true)
data class WireMockDelayRequest(
    val fixedDelay: Int
)

@JsonClass(generateAdapter = true)
data class WireMockMappingResponse(
    val body: String? = null,
    val bodyFileName: String? = null,
    val status: Int? = 200,
    val headers: WireMockResponseHeaders? = null,
    val fixedDelayMilliseconds: Int? = 0,
    val fault: String? = null
)

enum class WireMockFault {
    CONNECTION_RESET_BY_PEER,
    EMPTY_RESPONSE,
    RANDOM_DATA_THEN_CLOSE,
    MALFORMED_RESPONSE_CHUNK
}