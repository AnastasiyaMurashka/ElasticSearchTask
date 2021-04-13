package client

import com.jayway.restassured.RestAssured
import com.jayway.restassured.response.Response

class HttpClient {
    private static final String BASE_URL = 'http://localhost:9200/employees'

    HttpClient() {
        RestAssured.baseURI = BASE_URL
    }

    Response get(String body, String relativeUrl) {
        Map<String, String> requestHeaders = new HashMap()
        requestHeaders.put('Content-Type', 'application/json')
        requestHeaders.put('Accept', 'application/json')
        def request = RestAssured.given().headers(requestHeaders).body(body)
        return request.get(relativeUrl)
    }

    Response get(String relativeUrl) {
        Map<String, String> requestHeaders = new HashMap()
        requestHeaders.put('Content-Type', 'application/json')
        requestHeaders.put('Accept', 'application/json')
        def request = RestAssured.given().headers(requestHeaders)
        return request.get(relativeUrl)
    }
}
