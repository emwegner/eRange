package de.hsos.ma.erange

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//SHARED PREFERENCES
fun saveSharedPreference (context: Context, name: String, doubleVal: Double) {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    // Create JSON.
    val gson = Gson()
    val json = gson.toJson(doubleVal)
    // Save data toShared Prefs.
    editor.putString(name, json)
    editor.apply()
    val msg = String.format("'%s = %f' saved to preferences", name, doubleVal)
}

fun loadSharedPreference (context: Context, name: String): Double {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    // Access JSON formatted preference data.
    val gson = Gson()
    val json = sharedPreferences.getString(name, null)
    // Read value (type-safe).
    val type: Type = object : TypeToken<Double?>() {}.type
    val readVal = gson.fromJson<Any>(json, type)
    val result: Double = if (readVal != null) readVal as Double else 70.0
    return result
}

fun saveSharedPreferenceString (context: Context, name: String, string: String) {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    // Create JSON.
    val gson = Gson()
    val json = gson.toJson(string)
    // Save data toShared Prefs.
    editor.putString(name, json)
    editor.apply()
    // val msg = String.format("'%s = %f' saved to preferences", name, string)
}

fun loadSharedPreferenceString (context: Context, name: String): String {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    // Access JSON formatted preference data.
    val gson = Gson()
    val json = sharedPreferences.getString(name, null)
    // Read value (type-safe).
    val type: Type = object : TypeToken<String?>() {}.type
    var readVal = gson.fromJson<Any>(json, type)
    if(readVal == null) readVal = ""
    return readVal.toString()
}