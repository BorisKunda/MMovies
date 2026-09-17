package com.bk.mmovies.locale

import java.util.Locale

enum class AppLanguage(val languageTag: String, val tmdbLanguageTag: String, val locale: Locale) {
    ENGLISH("en", "en-US", Locale.ENGLISH),
    HEBREW("iw", "he-IL", Locale("iw")),
    RUSSIAN("ru", "ru-RU", Locale("ru"));

    companion object {
        // Android aliases "he"/"iw" for resource-qualifier matching, but
        // Locale.getLanguage() can report either depending on OS version/source,
        // so both must be accepted here.
        private val HEBREW_LANGUAGE_CODES = setOf("iw", "he")

        fun fromLocale(locale: Locale): AppLanguage = when (locale.language) {
            in HEBREW_LANGUAGE_CODES -> HEBREW
            RUSSIAN.languageTag -> RUSSIAN
            else -> ENGLISH
        }
    }
}
