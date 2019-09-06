package com.cft.app.xmppapp.app_helper

import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.packet.Presence
import org.jxmpp.jid.Jid
import java.text.SimpleDateFormat
import java.util.*
import android.R
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.math.min
import kotlin.math.roundToInt


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

    fun getScaledBitmapPath(context: Context, bitmap: Bitmap): String {

        val scaledBitmap = scaleDown(bitmap)
        val file: File
        var path = ""
        try{
            file = makeMediaFile(context)!!
            file.createNewFile()
            path = file.absolutePath
            val bytes = ByteArrayOutputStream()
            val outputStream = FileOutputStream(file)
            outputStream.write(bytes.toByteArray())
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            outputStream.flush()
            outputStream.close()
            return path
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return path
    }

    private fun makeMediaFile(context: Context): File? {
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
        val path = createDirectory(context)
        val imageFile = File("$path${File.separator}$fileName")

        try{
            if(imageFile.exists())
                imageFile.delete()
            imageFile.createNewFile()
            return imageFile
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    private fun scaleDown(realImage: Bitmap): Bitmap {

        val ratio = min(1000 / realImage.width, 1000 / realImage.height)
        val width = (ratio * realImage.width).toDouble().roundToInt()
        val height = (ratio * realImage.height).toDouble().roundToInt()
        return Bitmap.createScaledBitmap(realImage, width, height, true)
    }

    private fun createDirectory(context: Context): File {
        val state = Environment.getExternalStorageState()
        val directoryName = AppConstants.APP_NAME
        val folder: File
        if (state.contains(Environment.MEDIA_MOUNTED)) {
            folder =
                File("${Environment.getExternalStorageDirectory()}${File.separator}${directoryName}")
        } else {
            folder = File("${context.filesDir}${File.separator}${directoryName}");
        }

        if (folder.mkdir())
            return folder
        else
            return folder
    }
}