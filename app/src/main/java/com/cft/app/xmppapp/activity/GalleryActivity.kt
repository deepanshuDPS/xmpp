package com.cft.app.xmppapp.activity

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.app_helper.AppConstants.REQUEST_CODE_READ_WRITE_PERMISSION
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.cft.app.xmppapp.adapter.GalleryAdapter
import com.cft.app.xmppapp.model.GalleryModel
import kotlinx.android.synthetic.main.activity_gallery.*
import android.graphics.Point
import com.cft.app.xmppapp.listener.OnFileSelectedListener
import com.cft.app.xmppapp.listener.OnMyClickListener

/**
 * activity for choosing images and video from the list of files in external directory
 */
class GalleryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkForReadAndWritePermission())
            ActivityCompat.requestPermissions(this,
                arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_WRITE_PERMISSION
            )
        else
            getAllMediaData()

    }

    // get All media data from external and set it to recycler view
    @SuppressLint("Recycle")
    private fun getAllMediaData() {
        val list = ArrayList<GalleryModel>()
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x/2 - 4
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE
        )
        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        val imagesUri = MediaStore.Files.getContentUri("external")

        val cur = contentResolver.query(
            imagesUri,
            projection, // Selection arguments (none)
            selection        // Ordering
            , null, null
        )!!


        if (cur.moveToFirst()) {
            var data: String
            var date: String
            var mimeType:String
            val dataColumn = cur.getColumnIndex(
                MediaStore.Files.FileColumns.DATA
            )

            val dateColumn = cur.getColumnIndex(
                MediaStore.Files.FileColumns.DATE_ADDED
            )

            val mimeTypeColumn = cur.getColumnIndex(
                MediaStore.Files.FileColumns.MIME_TYPE
            )
            do {
                // Get the field values
                data = cur.getString(dataColumn)
                date = cur.getString(dateColumn)
                mimeType = cur.getString(mimeTypeColumn)
                // Do something with the values.
                Log.d(
                    "ListingImages", " data=" + data
                            + "  date_taken=" + date +"mimeType"+mimeType
                )
                list.add(GalleryModel(data,mimeType))
            } while (cur.moveToNext())

        }
        cur.close()

        rv_gallery_content.layoutManager = GridLayoutManager(this,2)
        rv_gallery_content.adapter = GalleryAdapter(this,list,width,object :OnFileSelectedListener{
            override fun onFileSelected(filePath: String) {
                val intent = Intent()
                intent.putExtra("file_path",filePath)
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        })

    }

    // checking for needed permissions is given or not
    private fun checkForReadAndWritePermission() = ActivityCompat.checkSelfPermission(
        this,
        READ_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
        this,
        WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionGranted = true
        if (requestCode == REQUEST_CODE_READ_WRITE_PERMISSION && grantResults.isNotEmpty()) {
            for (i in grantResults)
                permissionGranted = permissionGranted && (i == PackageManager.PERMISSION_GRANTED)
            if (!permissionGranted)
                displayToast(getString(R.string.all_permissions_are_not_granted))
            else
               getAllMediaData()

        }
    }


}
