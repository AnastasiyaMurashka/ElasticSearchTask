package utils

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.jayway.restassured.response.Response
import groovy.json.JsonSlurper

class UtilMap {

    static Map convertJsonToMap(Response response) {
        String jsonString = response.body.asString()
        def map = new JsonSlurper().parseText(jsonString)
        return map
    }

    static findAllValueByKey(Map maps, String key, List eva) {
        if (maps instanceof Map) {
            if (maps.containsKey(key)) {
                eva.add(maps.get(key))
            }
        }
        for (map in maps) {
            if (map.value instanceof Map) {
                findAllValueByKey(map.value as Map, key, eva)
            }
            if (map.value instanceof List) {
                for (itemList in map.value) {
                    if (itemList instanceof Map) {
                        findAllValueByKey(itemList as Map, key, eva)
                    }
                }
            }
        }
    }

    static findAllObjectWithKey(Map maps, String key, List eva) {
        if (maps instanceof Map) {
            if (maps.containsKey(key)) {
                eva.add(maps)
            }
        }
        for (map in maps) {
            if (map.value instanceof Map) {
                findAllObjectWithKey(map.value as Map, key, eva)
            }
            if (map.value instanceof List) {
                for (itemList in map.value) {
                    if (itemList instanceof Map) {
                        findAllObjectWithKey(itemList as Map, key, eva)
                    }
                }
            }
        }
    }

    //also we can get all values by keyword using JsonPath
    static findAllValuesWithJsonPath(Response response, String key) {
        String jsonString = response.body().asString()
        DocumentContext jsonContext = JsonPath.parse(jsonString)
        return jsonContext.read("\$..$key")
    }
}
