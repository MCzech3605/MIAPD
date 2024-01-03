package com.decisionmaking

import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


val serverIP = "http://192.168.0.10:8000"

var itemIds: Array<Int> = arrayOf()

var itemNames: Array<String> = arrayOf()

var itemDescriptions: Array<String> = arrayOf()

var criteriaIds: Array<Int> = arrayOf()

var criteriaNames: Array<String> = arrayOf()

var criteriaDescriptions: Array<String> = arrayOf()

var currentCriterion: Int = 0

var answers: Array<Double> = arrayOf()

var answersForServer: Array<Array<Double>> = arrayOf()

val proportions: Array<Double> =
    arrayOf(0.2, 0.25, (1.0 / 3.0), 0.5, (2.0 / 3.0), 1.0, 1.5, 2.0, 3.0, 4.0, 5.0)

var alternatives1: Array<String> = arrayOf("1.Example alt 1", "2.Example alt 1")

var alternatives2: Array<String> = arrayOf("1.Example alt2 ", "2.Example alt 2")

var rankingArray: Array<String> = arrayOf("Example item 1", "Example item 2", "Example item 3")

val headerSize = 30.sp
val headerPadding = 50.dp

fun getItems() {
    val url = URL("$serverIP/items")
    val con = url.openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    con.setRequestProperty("Content-Type", "application/json")

    val `in` = BufferedReader(
        InputStreamReader(con.inputStream)
    )
    var inputLine: String?
    val jsonString = StringBuffer()
    while (`in`.readLine().also { inputLine = it } != null) {
        jsonString.append(inputLine)
    }
    `in`.close()

    val json = JSONObject(jsonString.toString())
    val itemIds1 = json.getJSONArray("item_ids")
    val itemNames1 = json.getJSONArray("item_names")
    val itemDescriptions1 = json.getJSONArray("item_descriptions")

    val criteriaIds1 = json.getJSONArray("criteria_ids")
    val criteriaNames1 = json.getJSONArray("criteria_names")
    val criteriaDescriptions1 = json.getJSONArray("criteria_descriptions")

    itemIds = Array(itemIds1.length()) { i ->
        itemIds1.getInt(i)
    }

    itemNames = Array(itemNames1.length()) { i ->
        itemNames1.getString(i)
    }

    itemDescriptions = Array(itemDescriptions1.length()) { i ->
        itemDescriptions1.getString(i)
    }

    criteriaIds = Array(criteriaIds1.length()) { i ->
        criteriaIds1.getInt(i)
    }

    criteriaNames = Array(criteriaNames1.length()) { i ->
        criteriaNames1.getString(i)
    }

    criteriaDescriptions = Array(criteriaDescriptions1.length()) { i ->
        criteriaDescriptions1.getString(i)
    }

    con.disconnect()
}

fun writeAlternatives() {
    resetAlternatives()
    for (i in itemIds.indices) {
        for (j in i + 1 until itemIds.size) {
            alternatives1.plus(itemNames[i])
            alternatives2.plus(itemNames[j])
        }
    }
}

fun resetAlternatives() {
    alternatives1 = arrayOf()
    alternatives2 = arrayOf()
}

fun writeServerAnswers() {
    answersForServer = Array(itemIds.size) { Array(itemIds.size) { 1.0 } }
    var ommited = 0
    for (i in itemIds.indices) {
        ommited += i + 1
        for (j in i + 1 until itemIds.size) {
            val ind = i * itemIds.size + j - ommited
            answersForServer[i][j] = answers[ind]
            answersForServer[j][i] = 1.0 / answers[ind]
        }
    }
}

fun pushAnswers() {
    writeServerAnswers()

    val matrix = JSONArray()

    answersForServer.forEach { item ->
        matrix.put(JSONArray(item.toList()))
    }

    val idsList = JSONArray(itemIds.toList())

    val jsonObject = JSONObject()
    jsonObject.put("matrix", matrix)
    jsonObject.put("ids", idsList)

    val url = URL("$serverIP/comparison") // Put your URL here
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json; utf-8")
    connection.setRequestProperty("Accept", "application/json")
    connection.doOutput = true

    connection.outputStream.use { os ->
        val writer = OutputStreamWriter(os, "UTF-8")
        writer.write(jsonObject.toString())
        writer.flush()
        writer.close()
    }

    resetAnswers()
}

fun resetAnswers() {
    answers = arrayOf()
    answersForServer = arrayOf()
}

// answers as matrix sent to server with additional 1d array with indexes of compared features
fun sendUserFileToServer(file: Uri): Boolean {
    // TODO push .json file to server
    return true // return true if success, else return false
}

fun getRanking() {
    val url = URL("$serverIP/ranking")
    val con = url.openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    con.setRequestProperty("Content-Type", "application/json")

    val `in` = BufferedReader(
        InputStreamReader(con.inputStream)
    )
    var inputLine: String?
    val jsonString = StringBuffer()
    while (`in`.readLine().also { inputLine = it } != null) {
        jsonString.append(inputLine)
    }
    `in`.close()

    val json = JSONArray(jsonString.toString())

    rankingArray = Array(json.length()) { i ->
        json.getString(i)
    }

    con.disconnect()
    // rankingArray = ...
}
