package nz.ac.canterbury.seng303.as1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nz.ac.canterbury.seng303.as1.screens.CreateFlashcardScreen
import nz.ac.canterbury.seng303.as1.screens.CreateNoteScreen
import nz.ac.canterbury.seng303.as1.screens.FlashcardList
import nz.ac.canterbury.seng303.as1.screens.NoteCard
import nz.ac.canterbury.seng303.as1.screens.NoteGrid
import nz.ac.canterbury.seng303.as1.screens.NoteList
import nz.ac.canterbury.seng303.as1.ui.theme.Lab2Theme
import nz.ac.canterbury.seng303.as1.viewmodels.CreateFlashcardViewModel
import nz.ac.canterbury.seng303.as1.viewmodels.CreateNoteViewModel
import nz.ac.canterbury.seng303.as1.viewmodels.FlashcardViewModel
import nz.ac.canterbury.seng303.as1.viewmodels.NoteViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel

class MainActivity : ComponentActivity() {
    private val noteViewModel: NoteViewModel by koinViewModel()
    private val flashcardViewModel: FlashcardViewModel by koinViewModel()
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1000)
        installSplashScreen()
        noteViewModel.loadDefaultNotesIfNoneExist()
        flashcardViewModel.loadDefaultFlashcardsForTestingPurposes()
        setContent {
            Lab2Theme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                Scaffold(
                    topBar = {
                        // Add your AppBar content here
                        TopAppBar(
                            title = { Text("Flashcards") },
                            navigationIcon = {
                                if (navBackStackEntry?.destination?.route != "Home") {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        val createNoteViewModel: CreateNoteViewModel = viewModel()
                        val createFlashcardViewModel: CreateFlashcardViewModel = viewModel()
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController)
                            }
                            composable(
                                "NoteCard/{noteId}",
                                arguments = listOf(navArgument("noteId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val noteId = backStackEntry.arguments?.getString("noteId")
                                noteId?.let { noteIdParam: String -> NoteCard(noteIdParam, noteViewModel) }
                            }
                            composable("NoteList") {
                                NoteList(navController, noteViewModel)
                            }
                            composable("NoteGrid") {
                                NoteGrid(navController)
                            }
                            composable("CreateNote") {
                                CreateNoteScreen(
                                    navController = navController,
                                    title = createNoteViewModel.title,
                                    onTitleChange = {newTitle -> createNoteViewModel.updateTitle(newTitle)},
                                    content = createNoteViewModel.content,
                                    onContentChange = {newContent -> createNoteViewModel.updateContent(newContent)},
                                    createNoteFn = {title, content -> noteViewModel.createNote(title, content)}
                                )
                            }
                            composable("FlashcardList") {
                                FlashcardList(navController, flashcardViewModel)
                            }
                            composable("CreateFlashcard") {
                                CreateFlashcardScreen(
                                    navController = navController,
                                    initialTerm = createFlashcardViewModel.term,
                                    initialDefinitions = createFlashcardViewModel.answers,
                                    createFlashcardFn = {term, defs -> flashcardViewModel.createFlashcard(term, defs)}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Flashcards!")
        Button(onClick = { navController.navigate("CreateNote") }) {
            Text("Create Note")
        }
        Button(onClick = { navController.navigate("CreateFlashcard") }) {
            Text("Create Flashcard")
        }
        Button(onClick = { navController.navigate("NoteCard/1") }) {
            Text("Go to Note Card")
        }
        Button(onClick = { navController.navigate("FlashcardList") }) {
            Text("View Flashcards")
        }
        Button(onClick = { navController.navigate("NoteGrid") }) {
            Text("Play Flashcards")
        }
    }
}
