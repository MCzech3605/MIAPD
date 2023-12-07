package com.decisionmaking

var items: Array<String> = arrayOf()

var attributes: Array<String> = arrayOf()

var answers: Array<Double> = arrayOf()

val proportions: Array<Double> = arrayOf(0.2, 0.25, (1.0/3.0), 0.5, (2.0/3.0), 1.0, 1.5, 2.0, 3.0, 4.0, 5.0)

var alternatives1: Array<String> = arrayOf("1.Example alt 1", "2.Example alt 1")

var alternatives2: Array<String> = arrayOf("1.Example alt2 ", "2.Example alt 2")


fun getItemsAndAttributes(){
    // TODO get items and attributes from server
}

fun writeAlternatives(){
    // TODO write alternatives array based on items and attributes
}

fun pushAnswers() {
    // TODO push answers to server
    answers = arrayOf()
}

fun sendUserFileToServer(/* TODO argument file */) {
    // TODO push file to server
}
