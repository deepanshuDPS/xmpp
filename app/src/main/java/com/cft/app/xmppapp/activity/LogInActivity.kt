package com.cft.app.xmppapp.activity

import android.os.Bundle
import android.view.View
import com.cft.app.xmppapp.app_helper.AppPreferences
import com.cft.app.xmppapp.listener.XMPPConnectionListener
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import com.cft.app.xmppapp.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_progress_bar.*

class LogInActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(AppPreferences.getLogInStatus(this)){
            setConnectionToUser(AppPreferences.getUsername(this),AppPreferences.getPassword(this))
        }
        else
            layout_progress_bar.visibility = View.GONE

        bt_login.setOnClickListener {
            setConnectionToUser(getTextFromEditText(et_username),getTextFromEditText(et_password))
        }
    }

    private fun setConnectionToUser(username:String,password:String) {
        ManageConnections.setConnection(username,password,object: XMPPConnectionListener{
            override fun onConnected(message: String) {
                runOnUiThread {
                    //displayToast(message)
                    AppPreferences.setLogInStatus(baseContext,true)
                    AppPreferences.setUserData(baseContext,username,password)
                    switchActivity(HomeActivity::class.java)
                    finish()
                }

            }

            override fun onError(message: String) {
                runOnUiThread {
                    displayToast(message)
                }
            }

        })
    }



}
