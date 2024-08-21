package nz.ac.canterbury.seng303.as1.screens

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
fun ViewFlashcard(
    flashcardId: String,
    navController: NavController,
    flashcardViewModel: FlashcardViewModel
) {
    flashcardViewModel.getFlashcardById(flashcardId = flashcardId.toIntOrNull())
    val selectedFlashcardState by flashcardViewModel.selectedFlashcard.collectAsState(null)
    val flashcard: Flashcard? = selectedFlashcardState
    if (flashcard == null) {
        Text(
            text = "Loading flashcard...",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center
        )
    } else {
        FlashcardView(
            navController = navController,
            deleteFn = { id -> flashcardViewModel.deleteFlashcardById(id) },
            flashcard = flashcard
        )
    }
}

@Composable
fun FlashcardView(
    navController: NavController,
    deleteFn: (Int) -> Unit,
    flashcard: Flashcard
) {
    val context = LocalContext.current
    var flipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = ""
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(16.dp)
            .clickable { flipped = !flipped }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
            ) {
                // Front content
                if (!flipped) {
                    Text(
                        text = flashcard.term,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                } else {
                    Text(
                        text = flashcard.answers.firstOrNull { a -> a.isCorrect }?.text ?: "No answer available",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)

                    )
                }
            }
        }
    }
}

