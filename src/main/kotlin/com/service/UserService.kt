package com.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.token.GenerateToken
import com.utils.DataMangerUtils
import org.bson.Document
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

data class UserFullData(val username:String, val age:Int, val email:String, val userid:String)
data class UserInsertData(val username: String, val password: String, val age: Int, val email: String)

@ApplicationScoped
class UserService(): DataMangerUtils() {
    val mapper = jacksonObjectMapper()
    @Inject
    lateinit var mongoClient: MongoClient

    override fun collection(): MongoCollection<Document> {
        return mongoClient.getDatabase("UserData").getCollection("User")
    }

    fun getFullUserInfo(username:String, password:String): String? {
        val getData = this.checkData(mapOf("name" to username, "password" to password))
        return if (getData != null) {
            val userInfo = UserFullData(
                getData.getString("name"),
                getData.getInteger("age"),
                getData.getString("email"),
                getData.getObjectId("_id").toString()
            )
            return GenerateToken.main(userInfo)
        } else {
            null
        }
    }

    fun userInsertDatatoMap(data: UserInsertData): Map<String, Any> {
        return mapOf(
            "name" to data.username,
            "password" to data.password,
            "age" to data.age,
            "email" to data.email,
        )
    }
}