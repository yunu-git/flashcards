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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    navController: NavController,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    createNoteFn: (String, String) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        CreateNote(
            navController = navController,
            title = title,
            onTitleChange = onTitleChange,
            content = content,
            onContentChange = onContentChange,
            createNoteFn = createNoteFn
        )
    } else {
        CreateNoteHorizontal(
            navController = navController,
            title = title,
            onTitleChange = onTitleChange,
            content = content,
            onContentChange = onContentChange,
            createNoteFn = createNoteFn
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNote(
    navController: NavController,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    createNoteFn: (String, String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = {onTitleChange(it)},
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Content input
        OutlinedTextField(
            value = content,
            onValueChange = {onContentChange(it)},
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .fillMaxHeight()
                .weight(1f)
        )

        // Save button
        Button(
            onClick = {
                createNoteFn(title, content)
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Created note!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, id ->
                        onTitleChange("")
                        onContentChange("")
                        navController.navigate("noteList")
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
fun CreateNoteHorizontal(
    navController: NavController,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    createNoteFn: (String, String) -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Row for Title and Content input fields
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { onTitleChange(it) },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(end = 8.dp)
            )

            // Content input
            OutlinedTextField(
                value = content,
                onValueChange = { onContentChange(it) },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        // Save button
        Button(
            onClick = {
                createNoteFn(title, content)
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Created note!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, _ ->
                        onTitleChange("")
                        onContentChange("")
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
