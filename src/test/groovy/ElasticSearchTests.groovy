import client.HttpClient
import com.jayway.restassured.response.Response
import groovy.json.JsonOutput
import org.apache.commons.lang3.RandomStringUtils
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import utils.UtilMap

class ElasticSearchTests {
    private httpClient = new HttpClient()
    private String relativeUrl = '/_search'

    @DataProvider(name = "queryBodyWithExpectedResult")
    Object[][] createListInjectData() {
        def array = new Object[2][]
        array[0] = [JsonOutput.toJson([query: [match_all: {}]]), ['QUINLIVAN', 'HELT', 'MACINNES', 'RACHI', 'FECHTNER', 'GALINOO',
                                                                  'CARDINALLI', 'BONTE', 'FUDALA', 'SYVERTSEN']]
        array[1] = [JsonOutput.toJson([query: [match: [Designation: "Delivery Manager"]]]), ['GREAVER', 'RACHI', 'FECHTNER']]
        return array
    }

    @Test(dataProvider = 'queryBodyWithExpectedResult')
    void checkGetMethodTest(String requestBody, ArrayList expectedResult) {
        Response response = httpClient.get(requestBody, relativeUrl)
        def jsonMap = UtilMap.convertJsonToMap(response)
        ArrayList actualValues = new ArrayList<String>()
        UtilMap.findAllValueByKey(jsonMap, 'LastName', actualValues)
        Assert.assertEquals(response.statusCode(), 200,
                "Expected that status code is equal 200, but it equal $response.statusCode")
        Assert.assertEquals(actualValues, expectedResult, "Actual lastnames don't match expected lastnames. Expected: $expectedResult, " +
                "but received $actualValues")
    }

    @Test
    void checkUnsuccessfulGetMethodTest() {
        def id = RandomStringUtils.randomAlphabetic(10)
        Response response = httpClient.get("$relativeUrl/$id")
        Assert.assertEquals(response.statusCode(), 404,
                "Expected that status code is equal 404, but it equal $response.statusCode")
    }

    @Test
    void getMaxAgeTest() {
        def requestBody = JsonOutput.toJson(["aggs": ["max_id": ["max": ["field": "Age"]]], "size": 0])
        BigDecimal expectedMaxAge = 63.0
        Response response = httpClient.get(requestBody, relativeUrl)
        def jsonMap = UtilMap.convertJsonToMap(response)
        ArrayList actualValues = new ArrayList<String>()
        UtilMap.findAllValueByKey(jsonMap, 'max_id', actualValues)
        def actualMaxAge = actualValues.value[0]
        Assert.assertEquals(actualMaxAge, expectedMaxAge,
                "Expected that max age is $expectedMaxAge, but actualy is $actualMaxAge")
    }

    @Test
    void checkSortRequestTest() {
        def requestBody = JsonOutput.toJson([query: [match_all: {}], sort: [[Salary: "asc"]]])
        Response response = httpClient.get(requestBody, relativeUrl)
        def jsonMap = UtilMap.convertJsonToMap(response)
        ArrayList actualValues = new ArrayList<Integer>()
        UtilMap.findAllValueByKey(jsonMap, 'Salary', actualValues)
        for (int i = 0; i < actualValues.size() - 1; i++) {
            Assert.assertTrue(actualValues[i].toInteger() <= actualValues[i + 1].toInteger(),
                    "Incorrectly sorted response")
        }
    }

    @Test
    void checkGetAllValuesUsingJsonPath() {
        def expectedLastNames = ['QUINLIVAN', 'HELT', 'MACINNES', 'RACHI', 'FECHTNER', 'GALINOO',
                                 'CARDINALLI', 'BONTE', 'FUDALA', 'SYVERTSEN']
        def requestBody = JsonOutput.toJson([query: [match_all: {}]])
        Response response = httpClient.get(requestBody, relativeUrl)
        def actualLastNames = UtilMap.findAllValuesWithJsonPath(response, 'LastName')
        Assert.assertEquals(expectedLastNames, actualLastNames, "Actual lastnames don't match expected. Expected: $expectedLastNames, " +
                "but received $actualLastNames")
    }
}
