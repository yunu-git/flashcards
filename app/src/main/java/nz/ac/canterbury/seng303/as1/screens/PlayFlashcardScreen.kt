package nz.ac.canterbury.seng303.as1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.as1.models.Answer
import nz.ac.canterbury.seng303.as1.models.Flashcard
import nz.ac.canterbury.seng303.as1.viewmodels.FlashcardViewModel
import kotlin.random.Random

@Composable
fun PlayFlashcardScreen(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel
) {
    flashcardViewModel.getFlashcards()
    val configuration = LocalConfiguration.current
    val flashcards: List<Flashcard> by flashcardViewModel.flashcards.collectAsState(emptyList())
    val shuffledFlashcards = remember(flashcards) { flashcards.shuffled(Random) }
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) {
        VerticalPlayFlashcards(
            flashcards = shuffledFlashcards
        )
    } else {
        HorizontalPlayFlashcards(
            flashcards = shuffledFlashcards
        )
    }
}

@Composable
fun VerticalPlayFlashcards(
    flashcards: List<Flashcard>
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Answer?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    val flashcard = flashcards.getOrNull(currentIndex)

    flashcard?.let { card ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = card.term,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = when {
                            showResult && selectedAnswer != null && selectedAnswer!!.isCorrect -> Color.Green
                            showResult && selectedAnswer != null && !selectedAnswer!!.isCorrect -> Color.Red
                            else -> Color.Transparent
                        }
                    )
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            card.answers.forEach { answer ->
                AnswerButton(
                    answer = answer,
                    onClick = {
                        if (!showResult) {
                            selectedAnswer = answer
                            if (answer.isCorrect) score++
                            showResult = true
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showResult) {
                Text(
                    text = "Score: $score",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (currentIndex < flashcards.size - 1) {
                            currentIndex++
                            selectedAnswer = null
                            showResult = false
                        } else {
                            //TODO: show an end screen
                        }
                    }
                ) {
                    Text(text = "Next")
                }
            }
        }
    } ?: run {
        Text(text = "No flashcards available", modifier = Modifier.fillMaxSize(), color = Color.Red)
    }
}

@Composable
fun HorizontalPlayFlashcards(
    flashcards: List<Flashcard>
) {

}

@Composable
fun AnswerButton(
    answer: Answer,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = answer.text)
    }
}
