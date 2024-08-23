package nz.ac.canterbury.seng303.as1.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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

@Composable
fun PlayFlashcardScreen(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel
) {
    flashcardViewModel.getFlashcards()
    val context = LocalContext.current
    val flashcards: List<Flashcard> by flashcardViewModel.flashcards.collectAsState(emptyList())
    flashcardViewModel.restartGame()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (flashcards.isEmpty()) {
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
            PlayFlashcard(
                navController = navController,
                flashcards = flashcards,
                flashcardViewModel = flashcardViewModel,
                context = context
            )

        }
    }
}

@Composable
fun PlayFlashcard(
    navController: NavController,
    flashcards: List<Flashcard>,
    flashcardViewModel: FlashcardViewModel = viewModel(),
    context: Context
) {

    val currentIndex by flashcardViewModel.currentIndex.collectAsState()
    val selectedAnswer by flashcardViewModel.selectedAnswer.collectAsState()
    val score by flashcardViewModel.score.collectAsState()
    val showResult by flashcardViewModel.showResult.collectAsState()
    val showEndScreen by flashcardViewModel.showEndScreen.collectAsState()
    val answerResults by flashcardViewModel.answerResults.collectAsState()
    val selectedAnswers by flashcardViewModel.selectedAnswers.collectAsState()

    if (showEndScreen) {
        PlayFlashcardEndScreen(
            score = score,
            totalQuestions = flashcards.size,
            answerResults = answerResults,
            selectedAnswers = selectedAnswers,
            flashcards = flashcards,
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
                    .verticalScroll(rememberScrollState())
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
                    val message = if (selectedAnswer?.isCorrect == true) "Correct!" else "Incorrect!"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
    answerResults: Map<Int, Boolean>,
    selectedAnswers: List<String>,
    flashcards: List<Flashcard>,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        flashcards.forEachIndexed { index, card ->
            val result = answerResults[index]
            val resultText = when (result) {
                true -> "Correct"
                false -> "Incorrect"
                else -> "Not Answered"
            }
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Question ${index + 1}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodySmall,

                    )
                    Text(
                        text = flashcards[index].term,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    if (result == false) {
                        HorizontalDivider()
                        Text(
                            text = "Correct answer:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = flashcards[index].answers.first{a -> a.isCorrect}.text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        HorizontalDivider()
                        Text(
                            text = "You answered:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = selectedAnswers[index],
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                    }

                }

            }

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Restart")
        }
        Spacer(modifier = Modifier.height(8.dp))
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

