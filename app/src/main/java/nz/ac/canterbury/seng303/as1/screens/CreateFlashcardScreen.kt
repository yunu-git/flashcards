package nz.ac.canterbury.seng303.as1.screens

import android.app.AlertDialog
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.as1.R
import nz.ac.canterbury.seng303.as1.models.Answer
import nz.ac.canterbury.seng303.as1.viewmodels.CreateFlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashcardScreen(
    navController: NavController,
    createFlashcardFn: (String, List<Answer>) -> Unit,
    viewModel: CreateFlashcardViewModel
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT


    if (isPortrait) {
        VerticalCreateFlashcard(
            navController = navController,
            term = viewModel.term,
            onTermChange = { it -> viewModel.updateTerm(it) },
            definitions = viewModel.answers,
            onDefinitionChange = { newDefinitions -> viewModel.updateAnswers(newDefinitions) },
            createFlashcardFn = createFlashcardFn,
            viewModel = viewModel
        )
    } else {
        HorizontalCreateFlashcard(
            navController = navController,
            term = viewModel.term,
            onTermChange = { it -> viewModel.updateTerm(it) },
            definitions = viewModel.answers,
            onDefinitionChange = { newDefinitions -> viewModel.updateAnswers(newDefinitions) },
            createFlashcardFn = createFlashcardFn,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalCreateFlashcard(
    navController: NavController,
    term: String,
    onTermChange: (String) -> Unit,
    definitions: List<Answer>,
    onDefinitionChange: (List<Answer>) -> Unit,
    createFlashcardFn: (String, List<Answer>) -> Unit,
    viewModel: CreateFlashcardViewModel
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
            // Term text field
            OutlinedTextField(
                value = term,
                onValueChange = { onTermChange(it) },
                label = { Text("Term") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = term.isBlank()
            )

            definitions.forEachIndexed { index, answer ->
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
                            isError = answer.text.isBlank()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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

                            Spacer(modifier = Modifier.width(8.dp))

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
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))
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
                    val builder = AlertDialog.Builder(context)
                    createFlashcardFn(term, definitions)
                    builder.setMessage("Created flashcard!")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { dialog, _ ->
                            onTermChange("")
                            onDefinitionChange(listOf(Answer("", false), Answer("", false)))
                            navController.navigate("flashcardList")
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }

                    val alert = builder.create()
                    alert.show()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                enabled = term.isNotBlank() && definitions.any { d ->
                    d.isCorrect && d.text.isNotBlank()
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalCreateFlashcard(
    navController: NavController,
    term: String,
    onTermChange: (String) -> Unit,
    definitions: List<Answer>,
    onDefinitionChange: (List<Answer>) -> Unit,
    createFlashcardFn: (String, List<Answer>) -> Unit,
    viewModel: CreateFlashcardViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = term,
            onValueChange = { onTermChange(it) },
            label = { Text("Term") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            isError = term.isBlank()

        )

        definitions.forEachIndexed { index, answer ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
                        isError = answer.text.isBlank()
                    )


                    Spacer(modifier = Modifier.height(8.dp)) // Space between text field and checkbox

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = answer.isCorrect,
                            onCheckedChange = { isChecked ->
                                viewModel.updateCorrect(index, isChecked)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(ContextCompat.getColor(context, R.color.dark_green)))
                        )

                        Spacer(modifier = Modifier.width(8.dp)) // Space between checkbox and delete button

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


        Button(
            onClick = {
                val newDefinitions = definitions.toMutableList()
                newDefinitions.add(Answer(text = "", isCorrect = false))
                onDefinitionChange(newDefinitions)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "+ Add Definition")
        }

        Button(
            onClick = {
                val builder = AlertDialog.Builder(context)
                createFlashcardFn(term, definitions)
                builder.setMessage("Created flashcard!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, _ ->
                        onTermChange("")
                        onDefinitionChange(listOf(Answer("", false), Answer("", false)))
                        navController.navigate("flashcardList")
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                val alert = builder.create()
                alert.show()
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = term.isNotBlank() && definitions.any { d ->
                d.isCorrect && d.text.isNotBlank()
            }
        ) {
            Text(text = "Save")
        }
    }
}

