package com.raithabharosa.hub

import android.content.Context
import android.content.SharedPreferences

object LanguageManager {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_KANNADA = "kn"
    
    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
    }
    
    fun setLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }
    
    fun isKannada(context: Context): Boolean {
        return getLanguage(context) == LANGUAGE_KANNADA
    }
}