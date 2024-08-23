package nz.ac.canterbury.seng303.as1.screens

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.as1.models.Flashcard
import nz.ac.canterbury.seng303.as1.viewmodels.FlashcardViewModel

@Composable
fun ViewFlashcardScreen(
    flashcardId: String,
    navController: NavController,
    flashcardViewModel: FlashcardViewModel
) {
    val context = LocalContext.current
    flashcardViewModel.getFlashcardById(flashcardId = flashcardId.toIntOrNull())
    val selectedFlashcardState by flashcardViewModel.selectedFlashcard.collectAsState(null)
    val flashcard: Flashcard? = selectedFlashcardState

    var flipped by rememberSaveable { mutableStateOf(false) }

    var fabStateExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    if (flashcard != null) {
        Scaffold(
            floatingActionButton = {
                ExpandableFab(
                    navController = navController,
                    context = context,
                    flashcard = flashcard,
                    fabStateExpanded = fabStateExpanded,
                    onFabStateChange = {fabStateExpanded = it},
                    deleteFn = { id -> flashcardViewModel.deleteFlashcardById(id) }
                )

            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                FlashcardView(
                    flashcard = flashcard,
                    flipped = flipped,
                    onFlip = { flipped = !flipped }
                )

            }
        }
    } else {
        Text(
            text = "Could not find flashcard: $flashcardId",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun FlashcardView(
    flashcard: Flashcard,
    flipped: Boolean,
    onFlip: () -> Unit
) {

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
            .clickable { onFlip() }
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



@Composable
fun ExpandableFab(
    navController: NavController,
    context: Context,
    flashcard: Flashcard?,
    fabStateExpanded: Boolean,
    onFabStateChange: (Boolean) -> Unit,
    deleteFn: (Int) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (fabStateExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "rotate"
    )
    Box(
        contentAlignment = Alignment.BottomEnd

    ) {
        FloatingActionButton(
            onClick = {
                onFabStateChange(!fabStateExpanded)
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Show More",
                modifier = Modifier.rotate(rotation)
            )
        }
        AnimatedVisibility(
            visible = fabStateExpanded,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 72.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,

            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("EditFlashcard/${flashcard!!.id}")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                    )
                }
                //TODO: not sure why this doesn't work without the app crashing?
//                FloatingActionButton(
//                    onClick = {
//                        val builder = AlertDialog.Builder(context)
//                        builder.setMessage("Delete flashcard: \"${flashcard!!.term}\"?")
//                            .setCancelable(false)
//                            .setPositiveButton("Delete") { dialog, _ ->
//                                navController.navigate("FlashcardList")
//                                dialog.dismiss()
//                                deleteFn(flashcard.id)
//                            }
//                            .setNegativeButton("Cancel") { dialog, _ ->
//                                dialog.dismiss()
//                            }
//                        val alert = builder.create()
//                        alert.show()
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Delete",
//                    )
//                }
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                            putExtra(SearchManager.QUERY, flashcard!!.term)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search this term on Google",
                    )
                }
            }
        }
    }
}

