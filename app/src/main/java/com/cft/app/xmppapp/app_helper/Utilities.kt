package com.cft.app.xmppapp.app_helper

import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.packet.Presence
import org.jxmpp.jid.Jid
import java.text.SimpleDateFormat
import java.util.*
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

    // get name from jid
    fun getNameFromJid(jid: Jid): String {
        val tempJid = jid.asBareJid().toString()
        return (tempJid[0] - 32).toString() + tempJid.substring(1, tempJid.indexOf("@"))
    }

    // get name from string
    fun getNameFromJid(jid: String) = (jid[0] - 32).toString() + jid.substring(1, jid.indexOf("@"))

    /**
     * @jid send subscribed request to passed jid
     */
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

    /**
     * @timeInMillis find only date using time in millis
     */
    fun findDate(timeInMillis: Long): String {

        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }

    /**
     * @timeInMillis find only day using time in millis
     */
    fun findDayFromTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("EEEE", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }

    /**
     * @timeInMillis find only time using time in millis
     */
    fun findTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("h:mm a", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }

    /**
     * @timeInMillis find only date using time in millis
     */
    fun findDateFromTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("d/MM/yyyy", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("GMT+5:30")
        return sdf.format(date)
    }

    /**
     * @bitmap get scaled bitmap after saving it to xmpp directory
     */
    fun getScaledBitmapPath(context: Context, bitmap: Bitmap): String {

        val scaledBitmap = scaleDown(bitmap)
        val file: File
        var path = ""
        try {
            file = makeMediaFile(context)!!
            file.createNewFile()
            path = file.absolutePath
            val bytes = ByteArrayOutputStream()
            val outputStream = FileOutputStream(file)
            outputStream.write(bytes.toByteArray())
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            return path
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }

    // create a media file image
    private fun makeMediaFile(context: Context): File? {
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
        val path = createDirectory(context)
        val imageFile = File("$path${File.separator}$fileName")

        try {
            if (imageFile.exists())
                imageFile.delete()
            imageFile.createNewFile()
            return imageFile
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // scale down the real image bitmap
    private fun scaleDown(realImage: Bitmap): Bitmap {

        val ratio = min(1000 / realImage.width, 1000 / realImage.height)
        val width = (ratio * realImage.width).toDouble().roundToInt()
        val height = (ratio * realImage.height).toDouble().roundToInt()
        return Bitmap.createScaledBitmap(realImage, width, height, true)
    }

    //  create file directory for saving compressed images
    private fun createDirectory(context: Context): File {
        val state = Environment.getExternalStorageState()
        val directoryName = AppConstants.APP_NAME
        val folder: File
        folder = if (state.contains(Environment.MEDIA_MOUNTED)) {
            File("${Environment.getExternalStorageDirectory()}${File.separator}${directoryName}")
        } else {
            File("${context.filesDir}${File.separator}${directoryName}")
        }

        return if (folder.mkdir()) folder else folder
    }
}