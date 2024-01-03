package com.decisionmaking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.decisionmaking.ui.theme.DecisionMakingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCurrentCriterionFromFile()
        writeAlternatives()
        setContent {
            DecisionMakingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainMenu(stringResource(id = R.string.app_name))
                }
            }
        }
    }
}

@Composable
fun MainMenu(name: String, modifier: Modifier = Modifier) {
    val mContext = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Welcome to $name!",
            textAlign = TextAlign.Center,
            fontSize = headerSize,
            modifier = modifier.padding(headerPadding)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {

            ElevatedButton(
                onClick = {
                    val intent = Intent(mContext, FacilitatorActivity::class.java)
                    mContext.startActivity(intent)
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Facilitator area")
            }
            ElevatedButton(
                onClick = {
                    if (currentCriterion == 0)
                        writeAlternatives()
                    val intent = Intent(mContext, ExpertsActivity::class.java)
                    intent.putExtra("index", 0)
                    mContext.startActivity(intent)
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Experts area")
            }
            ElevatedButton(
                onClick = {
                    val intent = Intent(mContext, RankingActivity::class.java)
                    mContext.startActivity(intent)
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Ranking")
            }
            ElevatedButton(
                onClick = {
                    getRanking()
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Update ranking")
            }
            ElevatedButton(
                onClick = {
                    if (currentCriterion >= criteriaIds.size) {
                        getItems()
                        writeAlternatives()
                        val toast = Toast.makeText(
                            mContext,
                            "Successfully imported new items",
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                    } else {
                        val toast = Toast.makeText(
                            mContext,
                            "Cannot import new items, rate current items first",
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                    }
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text("Get new items")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    DecisionMakingTheme {
        MainMenu(stringResource(id = R.string.app_name))
    }
}

