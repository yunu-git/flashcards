package nz.ac.canterbury.seng303.as1.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.as1.R
import nz.ac.canterbury.seng303.as1.models.Answer
import nz.ac.canterbury.seng303.as1.models.Flashcard
import nz.ac.canterbury.seng303.as1.viewmodels.FlashcardViewModel
import kotlin.random.Random

@Composable
fun PlayFlashcardScreen(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel
) {
    flashcardViewModel.getShuffledFlashcards()
    val context = LocalContext.current
    val shuffledFlashcards: List<Flashcard> by flashcardViewModel.shuffledFlashcards.collectAsState(emptyList())

    if (shuffledFlashcards.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No flashcards available.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Text(
                text = "You can create some here.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Button(
                onClick = {navController.navigate("CreateFlashcard")}
            ) {
                Text(text = "Create Flashcards")
            }
        }
    } else {
            PlayFlashcards(
                navController = navController,
                flashcardViewModel = flashcardViewModel,
                context = context
            )

    }
}

@Composable
fun PlayFlashcards(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel = viewModel(),
    context: Context
) {

    val flashcards by flashcardViewModel.shuffledFlashcards.collectAsState()
    val currentIndex by flashcardViewModel.currentIndex.collectAsState()
    val selectedAnswer by flashcardViewModel.selectedAnswer.collectAsState()
    val score by flashcardViewModel.score.collectAsState()
    val showResult by flashcardViewModel.showResult.collectAsState()
    val showEndScreen by flashcardViewModel.showEndScreen.collectAsState()

    if (showEndScreen) {
        PlayFlashcardEndScreen(
            score = score,
            totalQuestions = flashcards.size,
            onRestart = {
                flashcardViewModel.restartGame()
            },
            onExit = {
                flashcardViewModel.restartGame()
                navController.navigate("Home")
            },
            context = context
        )
    } else {
        flashcards.getOrNull(currentIndex)?.let { card ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Question: ${currentIndex + 1} of ${flashcards.size}",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Text(
                    text = card.term,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Text(
                    text = "Score: $score",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                card.answers.forEach { answer ->
                    AnswerButton(
                        answer = answer,
                        onClick = {
                            if (!showResult) {
                                flashcardViewModel.selectAnswer(answer)
                            }
                        },
                        selectedAnswer = selectedAnswer == answer,
                        context = context,
                        showResult = showResult
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (showResult) {
                    Button(
                        onClick = {
                            flashcardViewModel.nextFlashcard()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Next")
                    }
                } else {
                    Button(
                        onClick = {
                            flashcardViewModel.submitAnswer()
                        },
                        enabled = selectedAnswer != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Submit")
                    }
                }
            }
        } ?: run {
            Text(text = "No flashcards available", modifier = Modifier.fillMaxSize(), color = Color.Red)
        }
    }
}

@Composable
fun AnswerButton(
    answer: Answer,
    onClick: () -> Unit,
    selectedAnswer: Boolean,
    context: Context,
    showResult: Boolean
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
                containerColor = when {
                    showResult && answer.isCorrect -> Color(ContextCompat.getColor(context, R.color.dark_green))
                    showResult && selectedAnswer -> Color(ContextCompat.getColor(context, R.color.incorrect_red))
                    !showResult && selectedAnswer -> Color(ContextCompat.getColor(context, R.color.selected_button_color))
                    else -> Color(ContextCompat.getColor(context, R.color.theme_color))
                }
            ),

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = answer.text)
    }
}

@Composable
fun PlayFlashcardEndScreen(
    score: Int,
    totalQuestions: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Game Over!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Your Score: $score / $totalQuestions",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Restart")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExit,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(ContextCompat.getColor(context, R.color.incorrect_red))
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Exit")
        }
    }
}

