package com.cft.app.xmppapp.activity

import android.os.Bundle
import android.view.View
import com.cft.app.xmppapp.app_helper.AppPreferences
import com.cft.app.xmppapp.listener.XMPPConnectionListener
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.app_helper.NetworkUtil
import com.cft.app.xmppapp.app_helper.Utilities
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_progress_bar.*


/**
 * activity for Login to XMPP and if already login then redirect to Home
 */
class LogInActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(NetworkUtil.getConnectivityStatus(this)){
            // if already login then set connection automatically
            if(AppPreferences.getLogInStatus(this))
                setConnectionToUser(AppPreferences.getUsername(this),AppPreferences.getPassword(this))
            else
                layout_progress_bar.visibility = View.GONE

            bt_login.setOnClickListener {
                if(isValidToLogin())
                    setConnectionToUser(getTextFromEditText(et_username),getTextFromEditText(et_password))
            }
        }
        else{
            displayToast("No Connection")
        }

    }

    // function to check validation
    private fun isValidToLogin(): Boolean {

        if(getTextFromEditText(et_username).isEmpty()){
            ti_username.error = getString(R.string.please_enter_username)
            return false
        }
        if(getTextFromEditText(et_password).isEmpty()){
            ti_password.error = getString(R.string.please_enter_password)
            return false
        }
        ti_password.error = null
        ti_username.error = null
        return true
    }

    /***
     * @username is the username without jid
     * @password is the password for login
     */
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
