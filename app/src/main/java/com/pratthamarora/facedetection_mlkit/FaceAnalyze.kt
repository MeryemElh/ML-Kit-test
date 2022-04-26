package com.pratthamarora.facedetection_mlkit

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import kotlinx.android.synthetic.main.activity_main.*

class FaceAnalyze(
    private val progressBar: ProgressBar,
    private val group: Group,
    val doWhenImageProcessed: () -> Unit
) {

    private lateinit var paint: Paint
    private lateinit var canvas: Canvas
    private lateinit var firebaseVisionImage: FirebaseVisionImage
    private lateinit var firebaseVisionFaceDetector: FirebaseVisionFaceDetector

    private val options by lazy {
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()
    }

    init {
        firebaseVisionFaceDetector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)
    }

    fun detectFace(bmp: Bitmap): Bitmap {

        val scaledBitmap = Bitmap.createScaledBitmap(
            bmp,
            480,
            480,
            true
        )
        var mBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true)

        canvas = Canvas(mBitmap)
        paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        firebaseVisionImage = FirebaseVisionImage.fromBitmap(mBitmap)
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener { faces ->
                when {
                    faces.isNullOrEmpty() -> {
                        mBitmap = null
                    }
                    else -> {
                        for (face in faces) {
                            canvas?.drawRect(face.boundingBox, paint)
                        }
                    }
                }

                doWhenImageProcessed()

            }
            .addOnFailureListener {
                Log.d(MainActivity.TAG, "detectFace: $it")
            }

        return mBitmap
    }
}