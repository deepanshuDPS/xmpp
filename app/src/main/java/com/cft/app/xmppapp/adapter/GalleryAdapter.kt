package com.cft.app.xmppapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cft.app.xmppapp.R
import kotlinx.android.extensions.LayoutContainer
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.cft.app.xmppapp.app_helper.Utilities
import com.cft.app.xmppapp.listener.OnFileSelectedListener
import com.cft.app.xmppapp.model.GalleryModel
import kotlinx.android.synthetic.main.item_gallery_content.*
import java.io.File


class GalleryAdapter(private var context: Context, private var mediaList:ArrayList<GalleryModel>,val width:Int,private var onFileSelectedListener: OnFileSelectedListener) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gallery_content,parent,false))
    }

    override fun getItemCount() = mediaList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layout_content_uri.layoutParams.height = width
        holder.layout_content_uri.layoutParams.width = width

        val mediaPath = mediaList[position].mediaPath

        if(mediaList[position].mimeType.contains("image")){
            val bmOptions = BitmapFactory.Options()
            var bitmap = BitmapFactory.decodeFile(mediaList[position].mediaPath, bmOptions)
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
            holder.layout_content_uri.background = BitmapDrawable(context.resources, bitmap)
            holder.iv_mime_type.setBackgroundResource(R.drawable.ic_photo)
        }
        else{
            val bitmap = ThumbnailUtils.createVideoThumbnail(mediaList[position].mediaPath, MediaStore.Video.Thumbnails.MINI_KIND)
            holder.layout_content_uri.background = BitmapDrawable(context.resources, bitmap)
            holder.iv_mime_type.setBackgroundResource(R.drawable.ic_videocam)
        }

        holder.layout_content_uri.setOnClickListener {
            val file = File(mediaPath)
            val lengthInMB = file.length() / (1024f * 1024f)
            if (lengthInMB < 1f) {
                onFileSelectedListener.onFileSelected(mediaPath)
            } else {
                onFileSelectedListener.onFileSelected(Utilities.getScaledBitmapPath(context, BitmapFactory.decodeFile(mediaPath)))
            }
        }

    }

    class ViewHolder(override val containerView: View) :RecyclerView.ViewHolder(containerView),LayoutContainer
}