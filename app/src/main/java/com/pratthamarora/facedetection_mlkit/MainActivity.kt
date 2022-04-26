package com.pratthamarora.facedetection_mlkit

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val IMAGE_PICKER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectImageBtn.setOnClickListener {
            getImageFromLocal()
        }

    }

    private fun getImageFromLocal() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(this, IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                val bitmap = uri?.let { getBitmap(it) }
                bitmap?.let {
                    val analyzer = FaceAnalyze(progressBar, group, ::doWhenImageProcessed)

                    progressBar.visibility = View.VISIBLE
                    group.visibility = View.GONE
                    selectImageBtn.isEnabled = false

                    imageViewFace.setImageBitmap(analyzer.detectFace(it))
                }
            }
        }
    }

    fun doWhenImageProcessed() {
        progressBar.visibility = View.GONE
        group.visibility = View.VISIBLE
        selectImageBtn.isEnabled = true
    }

    private fun getBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }
}