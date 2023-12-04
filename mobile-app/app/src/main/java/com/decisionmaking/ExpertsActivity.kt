package com.decisionmaking

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.material3.Text
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.decisionmaking.ui.theme.DecisionMakingTheme

class ExpertsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alt1 = intent.getStringArrayExtra("alternatives1")
        val alt2 = intent.getStringArrayExtra("alternatives2")
        val ans = intent.getIntegerArrayListExtra("answers")
        val ind = intent.getIntExtra("index", -1)
        if (alt1.isNullOrEmpty() || alt2.isNullOrEmpty() || ans == null || ind < 0) {
            val toast: Toast = Toast.makeText(this, "Missing attributes!", Toast.LENGTH_LONG)
            toast.show()
            this.finish()
        } else if (alt1.size != alt2.size || ind >= alt1.size) {
            val toast: Toast =
                Toast.makeText(this, "Attributes sizes do not match!", Toast.LENGTH_LONG)
            toast.show()
            this.finish()
        }
        setContent {
            DecisionMakingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!(alt1.isNullOrEmpty() || alt2.isNullOrEmpty() || ans == null))
                        Choosing(alt1, alt2, ans, ind)
                }
            }
        }
    }
}

@Composable
fun Choosing(
    alternatives1: Array<String>,
    alternatives2: Array<String>,
    answers: ArrayList<Int>,
    ind: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select better alternative from the following:",
            modifier = Modifier.padding(30.dp)
        )
        ElevatedButton(
            onClick = { addAndFinish(alternatives1, alternatives2, answers, ind, 1, context) },
            Modifier.padding(10.dp)
        ) {
            Text(text = alternatives1[ind])
        }
        ElevatedButton(
            onClick = { addAndFinish(alternatives1, alternatives2, answers, ind, 2, context) },
            Modifier.padding(10.dp)
        ) {
            Text(text = alternatives2[ind])
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    DecisionMakingTheme {
        val a1 = arrayOf("OPTION ONE VERY LONG")
        val a2 = arrayOf("OPTION 2")
        Choosing(a1, a2, arrayListOf<Int>(), 0)
    }
}

fun addAndFinish(
    alternatives1: Array<String>, alternatives2: Array<String>,
    answers: ArrayList<Int>, ind: Int, ans: Int, context: Context
) {
    answers.add(ans)
    if (ind == alternatives1.size - 1) {
        pushAnswers(answers)
        val toast =
            Toast.makeText(context, "Answers inserted correctly", Toast.LENGTH_LONG)
        toast.show()
        context.getActivity()?.finish()
        return
    }
    val intent = Intent(context, ExpertsActivity::class.java)
    intent.putExtra("alternatives1", alternatives1)
    intent.putExtra("alternatives2", alternatives2)
    intent.putExtra("answers", answers)
    intent.putExtra("index", ind + 1)
    context.startActivity(intent)
    context.getActivity()?.finish()
}

fun pushAnswers(answers: ArrayList<Int>) {
    // todo
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

