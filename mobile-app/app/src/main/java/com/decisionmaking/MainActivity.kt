package com.decisionmaking

import android.content.Intent
import android.os.Bundle
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
        getItemsAndAttributes()
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Welcome to $name!",
            textAlign = TextAlign.Center,
            modifier = modifier.padding(100.dp)
        )
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
                val intent = Intent(mContext, ExpertsActivity::class.java)
                intent.putExtra("index", 0)
                mContext.startActivity(intent)
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Experts area")
        }
        ElevatedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Ranking")
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

