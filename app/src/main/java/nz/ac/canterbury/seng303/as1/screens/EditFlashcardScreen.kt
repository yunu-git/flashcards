package nz.ac.canterbury.seng303.as1.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.as1.R
import nz.ac.canterbury.seng303.as1.models.Answer
import nz.ac.canterbury.seng303.as1.models.Flashcard
import nz.ac.canterbury.seng303.as1.viewmodels.EditFlashcardViewModel
import nz.ac.canterbury.seng303.as1.viewmodels.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashcardScreen(
    flashcardId: String,
    navController: NavController,
    editFlashcardViewModel: EditFlashcardViewModel,
    flashcardViewModel: FlashcardViewModel
) {
    val selectedFlashcardState by flashcardViewModel.selectedFlashcard.collectAsState(null)
    val flashcard: Flashcard? = selectedFlashcardState // we explicitly assign to note to help the compilers smart cast out

    LaunchedEffect(flashcard) {
        if (flashcard == null) {
            flashcardViewModel.getFlashcardById(flashcardId.toIntOrNull())
        } else {
            editFlashcardViewModel.setDefaultValues(flashcard)
        }
    }

    EditFlashcard(
        flashcardId = flashcardId,
        navController = navController,
        term = editFlashcardViewModel.term,
        onTermChange = { it -> editFlashcardViewModel.updateTerm(it) },
        definitions = editFlashcardViewModel.answers,
        onDefinitionChange = { newDefinitions -> editFlashcardViewModel.updateAnswers(newDefinitions) },
        editFlashcardFn = {id, newFlashcard -> flashcardViewModel.editFlashcardById(id, newFlashcard)},
        editFlashcardViewModel = editFlashcardViewModel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashcard(
    flashcardId: String,
    navController: NavController,
    term: String,
    onTermChange: (String) -> Unit,
    definitions: List<Answer>,
    onDefinitionChange: (List<Answer>) -> Unit,
    editFlashcardFn: (Int?, Flashcard) -> Unit,
    editFlashcardViewModel: EditFlashcardViewModel,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .verticalScroll(scrollState),
        ) {
            OutlinedTextField(
                value = term,
                onValueChange = { onTermChange(it) },
                label = { Text("Term") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = term.isBlank(),
                supportingText = {
                    if (term.isBlank()) {
                        Text(
                            text = "Please enter a term.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            definitions.forEachIndexed { index, answer ->
                DefinitionEntry(
                    definitions = definitions,
                    index = index,
                    answer = answer,
                    viewModel = editFlashcardViewModel,
                    onDefinitionChange = onDefinitionChange,
                    context = context
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Button(
                onClick = {
                    val newDefinitions = definitions.toMutableList()
                    newDefinitions.add(Answer(text = "", isCorrect = false))
                    onDefinitionChange(newDefinitions)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "+ Add Definition")
            }

            Button(
                onClick = {
                    if (term.isNotBlank() && definitions.any { d ->
                            d.isCorrect && d.text.isNotBlank() }) {
                        editFlashcardFn(
                            flashcardId.toIntOrNull(),
                            Flashcard(
                                flashcardId.toInt(),
                                editFlashcardViewModel.term,
                                editFlashcardViewModel.answers
                            )
                        )
                        navController.navigate("flashcardList")
                        Toast.makeText(context, "Edited flashcard!", Toast.LENGTH_LONG).show()
                        onTermChange("")
                        onDefinitionChange(listOf(Answer("", false), Answer("", false)))
                    } else {
                        Toast.makeText(context, "Please add a term, at least two definitions, and one answer.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (term.isNotBlank() && definitions.any { d -> d.isCorrect && d.text.isNotBlank() }) Color(ContextCompat.getColor(context, R.color.theme_color)) else Color.LightGray
                )
            ) {
                Text(text = "Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefinitionEntry(
    definitions: List<Answer>,
    index: Int,
    answer: Answer,
    viewModel: EditFlashcardViewModel,
    onDefinitionChange: (List<Answer>) -> Unit,
    context: Context
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = answer.text,
                onValueChange = { newDefinition ->
                    val newDefinitions = definitions.toMutableList()
                    newDefinitions[index] = answer.copy(text = newDefinition, isCorrect = answer.isCorrect)
                    onDefinitionChange(newDefinitions)
                },
                label = { Text("Definition") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = answer.text.isBlank(),
                supportingText = {
                    if (answer.text.isBlank()) {
                        Text(
                            text = "Please enter an answer.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (answer.isCorrect) {
                    Text(
                        text = "Correct!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Checkbox(

                    checked = answer.isCorrect,
                    onCheckedChange = { isChecked ->
                        viewModel.updateCorrect(index, isChecked)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(ContextCompat.getColor(context, R.color.dark_green)))
                )

                if (definitions.size > 2) {
                    IconButton(
                        onClick = {
                            viewModel.removeAnswer(index)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Delete",
                            tint = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}




