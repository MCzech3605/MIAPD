package com.decisionmaking

import android.content.res.Resources
import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

const val serverIP = "http://192.168.0.10:8000"

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
    itemIds = arrayOf(4,5,6,7)

    itemNames = arrayOf("Ford", "Toyota", "Fiat", "Mercedes")

    itemDescriptions = arrayOf("American car", "Japanese car", "Italian car", "German car")

    criteriaIds = arrayOf(3,4,5)

    criteriaNames = arrayOf("fuel capacity", "price", "space")

    criteriaDescriptions = arrayOf("fuel capacity as given in the manual", "total price with taxes",
        "space inside the vehicle")

    currentCriterion = -1
}

fun writeAlternatives() {
    resetAlternatives()
    if (currentCriterion == -1) {
        for (i in criteriaIds.indices) {
            for (j in i + 1 until criteriaIds.size) {
                alternatives1 += criteriaNames[i] + " - " + criteriaDescriptions[i]
                alternatives2 += criteriaNames[j] + " - " + criteriaDescriptions[j]
            }
        }
        return
    }
    for (i in itemIds.indices) {
        for (j in i + 1 until itemIds.size) {
            alternatives1 += itemNames[i] + " - " + itemDescriptions[i]
            alternatives2 += itemNames[j] + " - " + itemDescriptions[j]
        }
    }
}

fun resetAlternatives() {
    alternatives1 = arrayOf()
    alternatives2 = arrayOf()
}

fun writeServerAnswers() {
    if (currentCriterion == -1) {
        answersForServer = Array(criteriaIds.size) { Array(criteriaIds.size) { 1.0 } }
        var omitted = 0
        for (i in criteriaIds.indices) {
            omitted += i + 1
            for (j in i + 1 until criteriaIds.size) {
                val ind = i * criteriaIds.size + j - omitted
                answersForServer[i][j] = answers[ind]
                answersForServer[j][i] = 1.0 / answers[ind]
            }
        }
        return
    }
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

fun pushAnswers() {
    writeServerAnswers()

    print(answersForServer)

    resetAnswers()
}

fun resetAnswers() {
    answers = arrayOf()
    answersForServer = arrayOf()
}

fun sendFacilitatorFileToServer(file: Uri): Boolean {
    // TODO push .json file to server, where .json includes data about items and criteria
    return true // return true if success, else return false
}

fun getRanking() {
     rankingArray = arrayOf("Mercedes", "Toyota", "Fiat", "Ford")
}
