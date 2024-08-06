package nz.ac.canterbury.seng303.as1.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.as1.models.Note
import nz.ac.canterbury.seng303.as1.viewmodels.NoteViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notes_data")

@FlowPreview
val dataAccessModule = module {
    single<Storage<Note>> {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<Note>>() {}.type,
            preferenceKey = stringPreferencesKey("notes"),
            dataStore = androidContext().dataStore
        )
    }

    single { Gson() }

    viewModel {
        NoteViewModel(noteStorage = get())
    }
}