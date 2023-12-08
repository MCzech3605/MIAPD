package com.decisionmaking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.decisionmaking.ui.theme.DecisionMakingTheme
import com.decisionmaking.ui.theme.Teal200

class RankingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DecisionMakingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Ranking(rankingArray)
                }
            }
        }
    }
}

@Composable
fun Ranking(items: Array<String>) {
    val mContext = LocalContext.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ranking",
            fontSize = 20.sp,
            modifier = Modifier.padding(40.dp)
        )
        if (items.isNotEmpty())
            TableScreen(items)
        else
            Text(text = "Ranking is currently empty")
        ElevatedButton(
            onClick = {
                mContext.getActivity()?.finish()
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Main Menu")
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun TableScreen(items: Array<String>) {
    // Just a fake data... a Pair of Int and String
    val tableData = (1..items.size).mapIndexed { index, item ->
        index to items[index]
    }
    // Each cell of a column must have the same weight.
    val column1Weight = .3f // 30%
    val column2Weight = .7f // 70%
    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Here is the header
        item {
            Row(Modifier.background(Teal200)) {
                TableCell(text = "Place", weight = column1Weight)
                TableCell(text = "Item name", weight = column2Weight)
            }
        }
        // Here are all the lines of your table.
        items(tableData) {
            val (id, text) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = (id + 1).toString(), weight = column1Weight)
                TableCell(text = text, weight = column2Weight)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    DecisionMakingTheme {
        Ranking(arrayOf("Item 1", "Item 2", "Item 3"))
    }
}

