package com.decisionmaking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.decisionmaking.ui.theme.DecisionMakingTheme

class FacilitatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DecisionMakingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Facilitation()
                }
            }
        }
    }
}

@Composable
fun Facilitation() {
    val mContext = LocalContext.current
    var visible by remember { mutableStateOf(true) }
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Facilitator area",
                fontSize = 20.sp,
                modifier = Modifier.padding(20.dp)
            )
            ElevatedButton(
                onClick = {
                    loadFile()
                    sendUserFileToServer()
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Send file to server")
            }
            ElevatedButton(
                onClick = {
                    visible = false
                    mContext.getActivity()?.finish()
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Main Menu")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DecisionMakingTheme {
        Facilitation()
    }
}

fun loadFile() {
    // TODO implementation
}