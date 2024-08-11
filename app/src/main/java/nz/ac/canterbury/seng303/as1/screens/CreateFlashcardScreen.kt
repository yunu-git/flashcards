package nz.ac.canterbury.seng303.as1.screens

import android.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    term: String,
    onTermChange: (String) -> Unit,
    definitions: List<Answer>,
    onDefinitionChange: (List<Answer>) -> Unit,
    createFlashcardFn: (String, List<Answer>) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        CreateFlashcard(
            navController = navController,
            term = term,
            onTermChange = onTermChange,
            definitions = definitions,
            onDefinitionChange = onDefinitionChange,
            createFlashcardFn = createFlashcardFn
        )
    } else {
        CreateFlashcardHorizontal(
            navController = navController,
            term = term,
            onTermChange = onTermChange,
            definitions = definitions,
            onDefinitionChange = onDefinitionChange,
            createFlashcardFn = createFlashcardFn
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashcard(
    navController: NavController,
    term: String,
    onTermChange: (String) -> Unit,
    definitions: List<Answer>,
    onDefinitionChange: (List<Answer>) -> Unit,
    createFlashcardFn: (String, List<Answer>) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = term,
            onValueChange = {onTermChange(it)},
            label = { Text("term") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )



        // Save button
        Button(
            onClick = {
                createFlashcardFn(term, definitions)
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Created flashcard!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, id ->
                        onTermChange("")
                        onDefinitionChange(emptyList())
                        navController.navigate("flashcardList")
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
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
fun CreateFlashcardHorizontal(
    navController: NavController,
    term: String,
    onTermChange: (String) -> Unit,
    definitions: List<Answer>,
    onDefinitionChange: (List<Answer>) -> Unit,
    createFlashcardFn: (String, List<Answer>) -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Row for term and Content input fields
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // term input
            OutlinedTextField(
                value = term,
                onValueChange = { onTermChange(it) },
                label = { Text("term") },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(end = 8.dp)
            )

            // Content input
        }

        // Save button
        Button(
            onClick = {
                createFlashcardFn(term, definitions)
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Created note!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, _ ->
                        onTermChange("")
                        onDefinitionChange(emptyList())
                        navController.navigate("noteList")
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
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
