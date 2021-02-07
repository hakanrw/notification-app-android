package tr.k12.enka.networking

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import net.grandcentrix.tray.AppPreferences

object PreferenceSingleton : Application() {

    lateinit var prefs: AppPreferences

    private const val PREFS_NAME = "params"

    fun init(context: Context) {
        if(!this::prefs.isInitialized) prefs = AppPreferences(context)
    }

}