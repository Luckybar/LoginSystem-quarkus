package com.service

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.utils.DataMangerUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

data class TaskInsertData(val userId: String, val finish: Boolean, val taskContent: String, val taskDate: String)

@ApplicationScoped
class TaskService(): DataMangerUtils() {
    @Inject
    lateinit var mongoClient: MongoClient

    override fun collection(): MongoCollection<Document> {
        return mongoClient.getDatabase("UserData").getCollection("Task")
    }

    fun getAllTask(userid: String): List<Document> {
        val filter = Document("userId", userid)
        val documentList = collection().find(filter).toList()
        documentList.forEach{ it ->
            it["_id"] = it.getObjectId("_id").toString()
        }
        return (documentList)
    }

    fun taskInsertDataToMap(taskInsertData: TaskInsertData): Map<String, Any> {
        return mapOf(
            "userId" to taskInsertData.userId,
            "finish" to taskInsertData.finish,
            "taskContent" to taskInsertData.taskContent,
            "taskDate" to taskInsertData.taskDate
        )
    }

    fun taskInsertMany(taskList: List<Map<String, Any>>): String {
        val result = runBlocking{
            val job = async {
                try {
                    insertDataMany(taskList)
                    return@async "insert success"
                }catch (e: Exception){
                    e.printStackTrace()
                    return@async "insert failed"
                }
            }
            return@runBlocking job.await()
        }
        return result
    }

    fun updateTaskData(updateMap: MutableMap<String, TaskInsertData>): String {
        val result = runBlocking {
            try {
                for (key in updateMap.keys){
                    launch {
                        val updateContent = updateMap[key]?.let { taskInsertDataToMap(it) }
                        updateContent?.let {  updateDataOne(key, updateContent) }
                    }
                }
                return@runBlocking "insert success"
            }catch (e :Exception){
                e.printStackTrace()
                return@runBlocking "insert failed"
            }
        }
        return result
    }

    fun deleteTaskData(list: List<String>): String{
        val result = runBlocking {
            try {
                list.forEach{it ->
                    deleteDataOne(mapOf("_id" to ObjectId(it)))
                }
                return@runBlocking "delete success"
            }catch (e :Exception){
                e.printStackTrace()
                return@runBlocking "delete failed"
            }
        }
        return result
    }
}