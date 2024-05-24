package com.jans.tiles.app.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsUtil(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // String preference
    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun getString(key: String ): String {
        return sharedPreferences.getString(key, "")?:""
    }

    // Boolean preference
    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    // Int preference
    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    // Clear all preferences
    fun clearPreferences() {
        editor.clear().apply()
    }
}