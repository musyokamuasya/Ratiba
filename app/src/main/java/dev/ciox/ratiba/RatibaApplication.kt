package dev.ciox.ratiba

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class RatibaApplication : Application(), Configuration.Provider{
    override fun getWorkManagerConfiguration(): Configuration {
        TODO("Not yet implemented")
    }

    companion object {
        fun obtainUriForFile(context: Context, source: File): Uri {
            return FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider", source
            )
        }
    }

}