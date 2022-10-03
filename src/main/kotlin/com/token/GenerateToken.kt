package com.token


import com.service.UserFullData
import com.token.TokenUtils.generateTokenString
import java.io.StringReader
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonPatch
import javax.json.JsonReader

object GenerateToken {
    /**
     *
     * @param args - [0]: optional name of classpath resource for json document of claims to add; defaults to "/JwtClaims.json"
     * [1]: optional time in seconds for expiration of generated token; defaults to 300
     * @throws Exception
     */
    @Throws(Exception::class)
    @JvmStatic
    fun main(userInfo: UserFullData): String {
        val userContent: JsonObject = Json.createObjectBuilder()
            .add("id", userInfo.userid)
            .add("name", userInfo.username)
            .add("email", userInfo.email)
            .add("age", userInfo.age)
            .build()
        var claimsJson = readJsonResource()
        claimsJson = mergeJsonObjects(claimsJson, userContent)
        return generateTokenString(claimsJson)
    }

    @Throws(Exception::class)
    private fun readJsonResource(): JsonObject {
        try {
            val inputStream = javaClass.getResourceAsStream("/JwtClaims.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonReader: JsonReader = Json.createReader(StringReader(jsonString))
            val jsonObject: JsonObject = jsonReader.readObject()
            jsonReader.close()

            return jsonObject
        } catch (e: Exception) {
            throw Exception("Error reading JwtClaims.json", e)
        }
    }

    @Throws(Exception::class)
    private fun mergeJsonObjects(obj1: JsonObject, obj2: JsonObject): JsonObject {
        val builder = Json.createObjectBuilder()
        for ((key, value) in obj1) {
            builder.add(key, value)
        }
        builder.add("userdata", obj2)
        return builder.build()
    }
}