import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

// Interface is used to generate new WireMock Admin calls via Retrofit
interface WireMockInterface {
    @POST("/__admin/mappings/reset")
    fun resetWireMockMappings(): Call<ResponseBody>

    @POST("/__admin/mappings")
    fun addNewMapping(@Body wireMockMapping: WireMockMapping): Call<ResponseBody>

    @POST("/__admin/settings")
    fun setWireMockDelay(@Body wireMockDelayRequest: WireMockDelayRequest): Call<ResponseBody>

    @POST("/__admin/requests/count")
    fun getRequestsByAccountCriteria(@Body wireMockCountCriteria: WireMockCountCriteria): Call<WireMockCountResponse>

    @DELETE("/__admin/requests")
    fun clearRequestsCount(): Call<ResponseBody>

    @POST("/__admin/scenarios/reset")
    fun resetScenarios(): Call<ResponseBody>

    companion object {
        fun start(): WireMockInterface {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://MY-BASE-URL.COM")
                .client(OkHttpClient())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(WireMockInterface::class.java)
        }
    }
}