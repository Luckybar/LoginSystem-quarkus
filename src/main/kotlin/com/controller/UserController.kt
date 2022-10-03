package com.controller
import com.service.UserService
import com.token.GenerateToken
import org.bson.Document
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.service.UserInsertData
import javax.json.JsonObject


@Path("/user")
class UserController (private val userService: UserService){

    @Path("checkuser")
    @GET
    fun checkUser(@QueryParam("username") username:String): Document? {
        return userService.checkData(mapOf("name" to username))
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun login(user: JsonObject): String? {
        val username = user.getString("username")
        val password = user.getString("password")
        return userService.getFullUserInfo(username, password)
    }

    @Path("register")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    fun register(user: JsonObject): String? {
        try {
            val userMap = jacksonObjectMapper().readValue(user.toString(), UserInsertData::class.java)
            val map = userService.userInsertDatatoMap(userMap)
            return userService.insertDataOne(map)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }
}