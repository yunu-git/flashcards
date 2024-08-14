package nz.ac.canterbury.seng303.as1.screens

import android.app.AlertDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.as1.models.Answer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashcardScreen(
    navController: NavController,
    initialTerm: String,
    initialDefinitions: List<Answer>,
    createFlashcardFn: (String, List<Answer>) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    // Maintain the state for term and definitions
    var term by remember { mutableStateOf(initialTerm) }
    var definitions by remember { mutableStateOf(initialDefinitions.toMutableList()) }

    if (isPortrait) {
        VerticalCreateFlashcard(
            navController = navController,
            term = term,
            onTermChange = { term = it },
            definitions = definitions,
            onDefinitionChange = { definitions = it.toMutableList() },
            createFlashcardFn = createFlashcardFn
        )
    } else {
        HorizontalCreateFlashcard(
            navController = navController,
            term = term,
            onTermChange = { term = it },
            definitions = definitions,
            onDefinitionChange = { definitions = it.toMutableList() },
            createFlashcardFn = createFlashcardFn
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
    createFlashcardFn: (String, List<Answer>) -> Unit
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
                .padding(bottom = 8.dp)
        )

        definitions.forEachIndexed { index, answer ->
            Row {
                Checkbox(
                    checked = answer.isCorrect,
                    onCheckedChange = {answer.isCorrect = it}
                )
                OutlinedTextField(
                    value = answer.text,
                    onValueChange = { newDefinition ->
                        val newDefinitions = definitions.toMutableList()
                        newDefinitions[index] = answer.copy(text = newDefinition, isCorrect = answer.isCorrect)
                        onDefinitionChange(newDefinitions)
                    },
                    label = { Text("Definition ${index + 1}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
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
                if (definitions.size < 2) {
                    Log.w("FLASHCARD_VIEW_MODEL", "There must be at least two definitions.")
                    builder.setMessage("There must be at least two definitions.")
                        .setCancelable(false)
                        .setPositiveButton("Ok") {dialog, _ ->
                            dialog.dismiss()
                        }
                } else {
                    createFlashcardFn(term, definitions)
                    builder.setMessage("Created flashcard!")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { dialog, _ ->
                            onTermChange("")
                            onDefinitionChange(emptyList())
                            navController.navigate("flashcardList")
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                }
                val alert = builder.create()
                alert.show()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Save")
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
    createFlashcardFn: (String, List<Answer>) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = term,
                onValueChange = { onTermChange(it) },
                label = { Text("Term") },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(end = 8.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                definitions.forEachIndexed { index, answer ->
                    OutlinedTextField(
                        value = answer.text,
                        onValueChange = { newDefinition ->
                            val newDefinitions = definitions.toMutableList()
                            newDefinitions[index] = answer.copy(text = newDefinition)
                            onDefinitionChange(newDefinitions)
                        },
                        label = { Text("Definition ${index + 1}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
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
            }
        }

        Button(
            onClick = {
                val builder = AlertDialog.Builder(context)
                if (definitions.size < 2) {
                    Log.w("FLASHCARD_VIEW_MODEL", "There must be at least two definitions.")
                    builder.setMessage("There must be at least two definitions.")
                        .setCancelable(false)
                        .setPositiveButton("Ok") {dialog, _ ->
                            dialog.dismiss()
                        }
                } else {
                    createFlashcardFn(term, definitions)
                    builder.setMessage("Created flashcard!")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { dialog, _ ->
                            onTermChange("")
                            onDefinitionChange(emptyList())
                            navController.navigate("flashcardList")
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                }

                val alert = builder.create()
                alert.show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Save")
        }
    }
}
