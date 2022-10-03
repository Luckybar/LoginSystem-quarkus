package com.utils

import com.mongodb.client.MongoCollection
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId

abstract class DataMangerUtils {

    abstract fun collection(): MongoCollection<Document>

    fun checkData(map: Map<String, Any>): Document? {
        val document = Document()
        map.forEach { (key, value) ->
            document.append(key, value)
        }
        return collection().find(document).first()
    }

    fun insertDataOne(map: Map<String, Any>) :String?{
        return try {
            val document = Document()
            map.forEach { (key, value) ->
                document.append(key, value)
            }
            collection().insertOne(document)
            "insert success"
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun updateDataOne(taskId: String, map2: Map<String, Any>) {
        val document = Document()
        document.append("_id", ObjectId(taskId))
        val document2 = Document()
        map2.forEach { (key, value) ->
            document2.append(key, value)
        }
        val updateDoc = Document("\$set", document2)
        collection().updateOne(document, updateDoc)
    }

    fun deleteDataOne(map: Map<String, Any>) {
        val document = Document()
        map.forEach { (key, value) ->
            document.append(key, value)
        }
        collection().deleteOne(document)
    }

    suspend fun insertDataMany(dataList: List<Map<String, Any>>) {
        val listDocument = mutableListOf<Document>()
        dataList.forEach { map ->
            val document = Document()
            map.forEach { (key, value) ->
                document.append(key, value)
            }
            listDocument.add(document)
        }
        collection().insertMany(listDocument)
    }

}