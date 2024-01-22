package com.decisionmaking

import android.content.ContentResolver
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

const val serverIP = "http://192.168.1.22:8000"

var expertNick: String = ""

var expertId: Int = -1

var itemIds: Array<Int> = arrayOf()

var itemNames: Array<String> = arrayOf()

var itemDescriptions: Array<String> = arrayOf()

var criteriaIds: Array<Int> = arrayOf()

var criteriaNames: Array<String> = arrayOf()

var criteriaDescriptions: Array<String> = arrayOf()

var criteriaParentIds: Array<Int> = arrayOf()

var superCriteria: MutableSet<Int> = mutableSetOf(-1)

var currentCriterion: Int = -1

var currentCriterionId: Int = -1

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
    val response = StringBuilder()

    BufferedReader(InputStreamReader(con.inputStream)).use {
        var inputLine = it.readLine()
        while (inputLine != null) {
            response.append(inputLine)
            inputLine = it.readLine()
        }
    }

    val json = JSONObject(response.toString())
    val itemIds1 = json.getJSONArray("item_ids")
    val itemNames1 = json.getJSONArray("item_names")
    val itemDescriptions1 = json.getJSONArray("item_descriptions")

    val criteriaIds1 = json.getJSONArray("criteria_ids")
    val criteriaNames1 = json.getJSONArray("criteria_names")
    val criteriaDescriptions1 = json.getJSONArray("criteria_descriptions")
    val criteriaParentIds1 = json.getJSONArray("criteria_parent_ids")

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

    criteriaParentIds = Array(criteriaParentIds1.length()) { i ->
        if (criteriaParentIds1.isNull(i)) {
            -1
        } else {
            superCriteria.add(criteriaParentIds1.getInt(i))
            criteriaParentIds1.getInt(i)
        }
    }
    con.responseCode
    con.disconnect()

    currentCriterion = -1
    currentCriterionId = -1
}

fun writeAlternatives() {
    resetAlternatives()

    if (currentCriterionId in superCriteria) {

        val childCriteria = criteriaIds.indices
            .filter { i -> criteriaParentIds[i] == currentCriterionId }
            .map { i -> Pair(criteriaNames[i], criteriaDescriptions[i]) }

        for (i in childCriteria.indices) {
            for (j in i + 1 until childCriteria.size) {
                alternatives1 += childCriteria[i].first + " - " + childCriteria[i].second
                alternatives2 += childCriteria[j].first + " - " + childCriteria[j].second
            }
        }
    } else {
        for (i in itemIds.indices) {
            for (j in i + 1 until itemIds.size) {
                alternatives1 += itemNames[i] + " - " + itemDescriptions[i]
                alternatives2 += itemNames[j] + " - " + itemDescriptions[j]
            }
        }
    }
}

fun resetAlternatives() {
    alternatives1 = arrayOf()
    alternatives2 = arrayOf()
}

fun writeServerAnswers() {

    if (currentCriterionId in superCriteria) {
        val childCriteria = criteriaIds.indices
            .filter { i -> criteriaParentIds[i] == currentCriterionId }

        answersForServer = Array(childCriteria.size) { Array(childCriteria.size) { 1.0 } }
        var omitted = 0
        for (i in childCriteria.indices) {
            omitted += i + 1
            for (j in i + 1 until childCriteria.size) {
                val ind = i * childCriteria.size + j - omitted
                answersForServer[i][j] = answers[ind]
                answersForServer[j][i] = 1.0 / answers[ind]
            }
        }
    } else {
        answersForServer = Array(itemIds.size) { Array(itemIds.size) { 1.0 } }
        var omitted = 0
        for (i in itemIds.indices) {
            omitted += i + 1
            for (j in i + 1 until itemIds.size) {
                val ind = i * itemIds.size + j - omitted
                answersForServer[i][j] = answers[ind]
                answersForServer[j][i] = 1.0 / answers[ind]
            }
        }
    }
}

fun pushAnswers() {
    writeServerAnswers()

    val matrix = JSONArray()

    answersForServer.forEach { item ->
        matrix.put(JSONArray(item.toList()))
    }

    val (idsList, url) = if (currentCriterionId in superCriteria) {
        val childCriteria = criteriaIds.indices
            .filter { i -> criteriaParentIds[i] == currentCriterionId }
            .map { i -> criteriaIds[i] }
        JSONArray(childCriteria) to URL("$serverIP/criteria_comparison")
    } else {
        JSONArray(itemIds.toList()) to URL("$serverIP/item_comparison")
    }

    val jsonObject = JSONObject()
    jsonObject.put("matrix", matrix)
    jsonObject.put("ids", idsList)
    jsonObject.put("criterionId", currentCriterionId)
    jsonObject.put("expertId", expertId)

    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.doOutput = true

    OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
        writer.write(jsonObject.toString())
        writer.flush()
    }
    connection.responseCode
    connection.disconnect()

    resetAnswers()
}

fun resetAnswers() {
    answers = arrayOf()
    answersForServer = arrayOf()
}

fun sendFacilitatorFileToServer(file: Uri, contentResolver: ContentResolver): Boolean {
    val inputStream = contentResolver.openInputStream(file)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val data = reader.use(BufferedReader::readText)

    val url = URL("$serverIP/facilitator_config")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.doOutput = true

    OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
        writer.write(data)
        writer.flush()
    }
    connection.responseCode
    connection.disconnect()

    return true // return true if success, else return false
}

fun getRanking() {
    val url = URL("$serverIP/ranking")
    val con = url.openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    val response = StringBuilder()

    BufferedReader(InputStreamReader(con.inputStream)).use {
        var inputLine = it.readLine()
        while (inputLine != null) {
            response.append(inputLine)
            inputLine = it.readLine()
        }
    }

    val json = JSONArray(response.toString())

    rankingArray = Array(json.length()) { i ->
        json.getString(i)
    }
    con.responseCode
    con.disconnect()
}

fun getExpertIdFromServer() {
    val url = URL("$serverIP/expert_id/$expertNick")
    val con = url.openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    val response = StringBuilder()

    BufferedReader(InputStreamReader(con.inputStream)).use {
        var inputLine = it.readLine()
        while (inputLine != null) {
            response.append(inputLine)
            inputLine = it.readLine()
        }
    }

    expertId = response.toString().toInt()
}
