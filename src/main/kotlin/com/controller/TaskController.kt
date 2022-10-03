package com.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.service.TaskInsertData
import com.service.TaskService
import org.bson.types.ObjectId
import javax.json.JsonObject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("task")
class TaskController (private val taskService: TaskService){

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("gettask")
    fun getTasks(@QueryParam("userid") userid: String): String? {
        val taskData = taskService.getAllTask(userid)
        return jacksonObjectMapper().writeValueAsString(taskData)
    }

    @PUT
    @Path("addtask")
    fun addTask(taskData:JsonObject):String?{
        val taskInsertData = jacksonObjectMapper().readValue(taskData.toString(), TaskInsertData::class.java)
        return taskService.insertDataOne(taskService.taskInsertDataToMap(taskInsertData))
    }

    @PUT
    @Path("addtaskmany")
    fun addTaskMany(taskData:JsonObject):String{
        val taskList = mutableListOf<TaskInsertData>()
        taskData.getJsonArray("task").forEach {
            val taskInsertData = jacksonObjectMapper().readValue(it.toString(), TaskInsertData::class.java)
            taskList.add(taskInsertData)
        }
        val mapOfTaskList: List<Map<String, Any>> = taskList.map { taskService.taskInsertDataToMap(it) }
        return taskService.taskInsertMany(mapOfTaskList)
    }

    @PUT
    @Path("updatetask")
    fun updateTask(taskData:JsonObject):String{
        val updateList = mutableMapOf<String, TaskInsertData>()
        val list = taskData.getJsonArray("task")
        for(i in  0 until list.size){
            val taskData = list.getJsonObject(i)
            val _id = taskData.getString("_id")
            val taskInsertData = jacksonObjectMapper().readValue(taskData.getJsonObject("content").toString(), TaskInsertData::class.java)
            updateList[_id] = taskInsertData
        }
        return taskService.updateTaskData(updateList)
    }

    @DELETE
    @Path("deletetask")
    fun deleteTask(taskData:JsonObject): String {
        val deleteList = mutableListOf<String>()
        for( i in taskData.getJsonArray("task")){
            var data = i.toString()
            data = data.replace("\"", "")
            deleteList.add(data)
        }
        return taskService.deleteTaskData(deleteList)
    }
}