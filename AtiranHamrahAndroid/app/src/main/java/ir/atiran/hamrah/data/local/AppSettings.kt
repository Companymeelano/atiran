package ir.atiran.hamrah.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "hamrah_settings")

/** تنظیمات ماندگار برنامه */
data class Prefs(
    val serverUrl: String = "",
    val demoMode: Boolean = true,
    val darkTheme: Boolean = true,
    val loggedIn: Boolean = false,
    val visitorName: String = "",
    val visitorRdf: Int = 0
)

/**
 * ذخیره‌سازی تنظیمات با DataStore.
 * هیچ‌گونه رمز عبور یا توکنی روی دستگاه ذخیره نمی‌شود (نکته امنیتی گزارش بازبینی).
 */
class AppSettings(private val context: Context) {

    private object Keys {
        val SERVER_URL = stringPreferencesKey("server_url")
        val DEMO_MODE = booleanPreferencesKey("demo_mode")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val VISITOR_NAME = stringPreferencesKey("visitor_name")
        val VISITOR_RDF = stringPreferencesKey("visitor_rdf")
    }

    val flow: Flow<Prefs> = context.dataStore.data.map { p ->
        Prefs(
            serverUrl = p[Keys.SERVER_URL] ?: "",
            demoMode = p[Keys.DEMO_MODE] ?: true,
            darkTheme = p[Keys.DARK_THEME] ?: true,
            loggedIn = p[Keys.LOGGED_IN] ?: false,
            visitorName = p[Keys.VISITOR_NAME] ?: "",
            visitorRdf = (p[Keys.VISITOR_RDF]?.toIntOrNull() ?: 0)
        )
    }

    suspend fun current(): Prefs = flow.first()

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[Keys.SERVER_URL] = url.trim() }
    }

    suspend fun setDemoMode(value: Boolean) {
        context.dataStore.edit { it[Keys.DEMO_MODE] = value }
    }

    suspend fun setDarkTheme(value: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = value }
    }

    suspend fun setLoggedIn(value: Boolean, name: String = "", rdf: Int = 0) {
        context.dataStore.edit {
            it[Keys.LOGGED_IN] = value
            it[Keys.VISITOR_NAME] = name
            it[Keys.VISITOR_RDF] = rdf.toString()
        }
    }
}
