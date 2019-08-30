package com.cft.app.xmppapp.app_helper

import android.content.Context

object AppPreferences {

    fun setLogInStatus(context: Context, status: Boolean) {
        val sharedPreferencesEditor =
            context.getSharedPreferences(AppConstants.USER_PREFERENCE, Context.MODE_PRIVATE).edit()
        sharedPreferencesEditor.putBoolean(AppConstants.LOGIN_STATUS, status)
        sharedPreferencesEditor.apply()
    }

    fun setUserData(context: Context,username:String,password:String){
        val sharedPreferencesEditor =
            context.getSharedPreferences(AppConstants.USER_PREFERENCE, Context.MODE_PRIVATE).edit()
        sharedPreferencesEditor.putString(AppConstants.USERNAME, username)
        sharedPreferencesEditor.putString(AppConstants.PASSWORD, password)
        sharedPreferencesEditor.apply()
    }



    fun getLogInStatus(context: Context) =
        context.getSharedPreferences(AppConstants.USER_PREFERENCE, Context.MODE_PRIVATE).getBoolean(
            AppConstants.LOGIN_STATUS,
            false
        )

    fun getUsername(context: Context) =
        context.getSharedPreferences(AppConstants.USER_PREFERENCE, Context.MODE_PRIVATE).getString(
            AppConstants.USERNAME,
            ""
        )!!

    fun getPassword(context: Context) =
        context.getSharedPreferences(AppConstants.USER_PREFERENCE, Context.MODE_PRIVATE).getString(
            AppConstants.PASSWORD,
            ""
        )!!

    fun clearSharedPreferences(context: Context) {
        val sharedPreferencesEditor =
            context.getSharedPreferences(AppConstants.USER_PREFERENCE, Context.MODE_PRIVATE).edit()
        sharedPreferencesEditor.clear()
        sharedPreferencesEditor.apply()
    }
}