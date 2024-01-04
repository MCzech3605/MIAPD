package com.decisionmaking

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.decisionmaking.ui.theme.DecisionMakingTheme

var ind: Int = 0

class ExpertsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ind = intent.getIntExtra("index", -1)
        if (alternatives1.isEmpty() || alternatives2.isEmpty() || ind < 0) {
            val toast: Toast = Toast.makeText(this, "Missing attributes!", Toast.LENGTH_LONG)
            toast.show()
            resetAnswers()
            this.finish()
        } else if (alternatives1.size != alternatives2.size || ind >= alternatives1.size) {
            val toast: Toast =
                Toast.makeText(this, "Attributes sizes do not match!", Toast.LENGTH_LONG)
            toast.show()
            resetAnswers()
            this.finish()
        }
        setContent {
            DecisionMakingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentCriterion < 0)
                        Choosing("comparison between two criteria")
                    else
                        Choosing(criteriaNames[currentCriterion])
                }
            }
        }
    }
}

@Composable
fun Choosing(criterion : String) {
    val context = LocalContext.current
    var sliderPosition by remember { mutableFloatStateOf(5f) }
    var visible by remember { mutableStateOf(true) }
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Use slider to express the preference between alternatives based on given " +
                        "criterion",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(30.dp)
            )
            Text(
                text = "Criterion: $criterion",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(30.dp)
            )
            Text(
                text = "Alternative 1: " + alternatives1[ind],
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(15.dp)
            )
            Text(
                text = "Alternative 2: " + alternatives2[ind],
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(15.dp)
            )
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                steps = 11,
                valueRange = 0f..10f
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Alternative 1",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Alternative 2",
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            ElevatedButton(
                onClick = {
                    visible = false
                    addAndFinish(proportions[sliderPosition.toInt()], context)
                },
                Modifier.padding(10.dp)
            ) {
                Text(text = "Click to confirm choice")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    DecisionMakingTheme {
        Choosing("here is the criterion description")
    }
}

fun addAndFinish(ans: Double, context: Context) {
    answers += ans
    if (ind == alternatives1.size - 1) {
        pushAnswers()
        val toast =
            Toast.makeText(context, "Answers inserted correctly", Toast.LENGTH_LONG)
        toast.show()
        currentCriterion++
        context.getActivity()?.finish()
        return
    }
    val intent = Intent(context, ExpertsActivity::class.java)
    intent.putExtra("index", ind + 1)
    context.startActivity(intent)
    context.getActivity()?.finish()
}


fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

