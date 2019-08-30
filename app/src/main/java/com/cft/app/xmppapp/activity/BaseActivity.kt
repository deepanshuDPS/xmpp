package com.cft.app.xmppapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cft.app.xmppapp.app_helper.AppPreferences
import com.cft.app.xmppapp.xmpp_connections.ManageConnections

abstract class BaseActivity : AppCompatActivity() {


    fun displayToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
    }

    fun displayToast(resId: Int) {
        displayToast(this.resources.getString(resId))
    }

    fun getTextFromEditText(editText: EditText) = editText.text.toString().trim()

    fun switchActivity(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
    }

    fun switchActivity(destinationActivity: Class<*>, bundle: Bundle?) {

        if (bundle == null)
            switchActivity(destinationActivity)
        else {
            val intent = Intent(this, destinationActivity).putExtras(bundle)
            startActivity(intent)
        }
    }


    fun logOut() {
        AppPreferences.clearSharedPreferences(this)
        ManageConnections.xMPPConnection?.disconnect()
    }

    /*override fun onDestroy() {
        super.onDestroy()
        ManageConnections.xMPPConnection?.disconnect()
    }*/
}