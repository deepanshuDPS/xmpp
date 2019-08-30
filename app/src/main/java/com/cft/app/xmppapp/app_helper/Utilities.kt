package com.cft.app.xmppapp.app_helper

import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.packet.Presence
import org.jxmpp.jid.Jid
import java.text.SimpleDateFormat
import java.util.*

object Utilities {

    fun getNameFromJid(jid: Jid): String {

        val tempJid = jid.asBareJid().toString()
        return (tempJid[0] - 32).toString() + tempJid.substring(1, tempJid.indexOf("@"))
    }

    fun getNameFromJid(jid: String) = (jid[0] - 32).toString() + jid.substring(1, jid.indexOf("@"))

    fun sendStanzaForSubscribed(jid: Jid) {

        val subscribedTo = Presence(Presence.Type.subscribed)
        subscribedTo.to = jid
        try {
            ManageConnections.xMPPConnection?.sendStanza(subscribedTo)
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun findDate(timeInMillis: Long): String {

        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }

    fun findDayFromTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("EEEE", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }

    fun findTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("h:mm a", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }

    fun findDateFromTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("d/MM/yyyy", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }
}