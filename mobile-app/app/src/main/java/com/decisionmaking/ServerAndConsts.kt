package com.decisionmaking

import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


var itemIds: Array<Int> = arrayOf()

var itemNames: Array<String> = arrayOf()

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
    // TODO get itemIds and itemNames from server
    // itemIds = ...
    // itemNames = ...
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
    // TODO push answersForServer to server with itemIds array
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
    // TODO import ranking to rankingArray like shown below:
    // rankingArray = ...
}
