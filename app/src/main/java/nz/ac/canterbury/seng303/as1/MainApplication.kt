package nz.ac.canterbury.seng303.as1

import android.app.Application
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.as1.datastore.dataAccessModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication: Application() {
    @OptIn(FlowPreview::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dataAccessModule)
        }
    }
}