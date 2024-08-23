package nz.ac.canterbury.seng303.as1.screens

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.as1.models.Flashcard
import nz.ac.canterbury.seng303.as1.viewmodels.FlashcardViewModel

@Composable
fun FlashcardList(navController: NavController, flashcardViewModel: FlashcardViewModel) {
    flashcardViewModel.getFlashcards()
    val flashcards: List<Flashcard> by flashcardViewModel.flashcards.collectAsState(emptyList())

    FlashcardList(
        navController = navController,
        flashcardViewModel = flashcardViewModel,
        flashcards = flashcards
    )
}

@Composable
fun FlashcardList(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel,
    flashcards: List<Flashcard>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (flashcards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
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
            }
        } else {
            LazyColumn {
                items(flashcards) { flashcard ->
                    Flashcard(
                        navController = navController,
                        deleteFn = { id -> flashcardViewModel.deleteFlashcardById(id) },
                        flashcard = flashcard
                    )
                }
            }
        }
    }
}


@Composable
fun Flashcard(
    navController: NavController,
    deleteFn: (Int) -> Unit,
    flashcard: Flashcard
) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("ViewFlashcard/${flashcard.id}")
            },
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
                text = flashcard.term,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, flashcard.term)
                }
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search this term on Google",
                    tint = Color.DarkGray
                )
            }
            IconButton(onClick = {
               navController.navigate("EditFlashcard/${flashcard.id}")
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = Color.DarkGray
                )
            }
            IconButton(onClick = {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Delete flashcard: \"${flashcard.term}\"?")
                    .setCancelable(false)
                    .setPositiveButton("Delete") { dialog, _ ->
                        deleteFn(flashcard.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.DarkGray
                )
            }
        }
    }

}