package com.yemen_restaurant.greenland.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

//val Context
// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class Storage(key:String) {
//    lateinit var  dataStore: DataStore<Preferences>
//    private val string = stringPreferencesKey(key)
//    val getString: Flow<String> = MyApplication.appContext.dataStore.data
//        .map { preferences ->
//            preferences[string] ?: ""
//        }
//    suspend fun setString(data: String) {
//       dataStore = createD
//        MyApplication.appContext.dataStore.edit { settings ->
//            settings[string] = data
//        }
//    }
}