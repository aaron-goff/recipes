// WireMockAdmin class is used to execute calls provided by the interface
class WireMockAdmin {
    companion object {
        private val service = WireMockInterface.start()

        fun addNewMapping(wireMockMapping: WireMockMapping) {
            service.addNewMapping(wireMockMapping).execute()
        }

        fun resetWireMockMappings() {
            service.resetWireMockMappings().execute()
        }

        fun setWireMockDelay(delay: Int) {
            service.setWireMockDelay(WireMockDelayRequest(delay)).execute()
        }

        fun getWireMockRequestsCountByCriteria(
            method: String, url: String? = null,
            urlPattern: String? = null,
            urlPath: String? = null,
            urlPathPattern: String? = null
        ): Int {
            val criteria = WireMockCountCriteria(
                method = method,
                url = url,
                urlPattern = urlPattern,
                urlPath = urlPath,
                urlPathPattern = urlPathPattern
            )
            val response = service.getRequestsByAccountCriteria(criteria).execute()
            return response.body()?.count!!
        }

        fun clearRequests() {
            service.clearRequestsCount().execute()
        }

        fun resetScenarios() {
            service.resetScenarios().execute()
        }
    }
}