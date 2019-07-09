package curso.carlos.indrivefordriver.helpers

import android.content.Context
import android.content.SharedPreferences

class VersionManager {

    val prefs: SharedPreferences
    val PREFS_NAME = "curso.carlos.indrivefordriver"
    val VERSION_VALUE = "APP_VERSION"
    var versionFromFirebase = "-1"

    constructor(version: String, context: Context) {
        versionFromFirebase = version
        prefs = context.getSharedPreferences(PREFS_NAME, 0)
    }

    fun isVersionUpdated(): Boolean {
        return prefs.getString(VERSION_VALUE, "").equals(versionFromFirebase)
    }

    fun updateVersionWithFirebase() {
        prefs.edit().putString(VERSION_VALUE, versionFromFirebase).apply()
    }

    companion object {
        val VERSION_PARAM_NAME = "APP_VERSION_PARAM"
    }

}