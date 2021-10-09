package com.adasoraninda.loginapplication.utils

import android.content.Context
import androidx.annotation.AttrRes
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.util.PatternsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

fun Context.getColorByAttribute(@AttrRes attr: Int): Int {
    withStyledAttributes(0, intArrayOf(attr)) {
        return getColorOrThrow(0)
    }

    throw IllegalArgumentException()
}

fun String.isValidEmail(): Boolean {
    return !isEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.length > 7
}