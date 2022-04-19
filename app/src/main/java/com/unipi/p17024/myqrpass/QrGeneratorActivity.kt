package com.unipi.p17024.myqrpass

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.unipi.p17024.myqrpass.MainActivity.Companion.sharedPreferencesMain
import com.unipi.p17024.myqrpass.databinding.ActivityQrGeneratorBinding

class QrGeneratorActivity : Activity() {
    private lateinit var binding: ActivityQrGeneratorBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var databaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_generator)
        binding = ActivityQrGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smart-e-tickets-android-wearos-default-rtdb.firebaseio.com/")

        //reading userID from Shared Preferences
        val userId = sharedPreferencesMain.getString("userID","default")
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val phone = firebaseUser?.phoneNumber
        //val userID = firebaseUser?.uid


        //Updating imageView's image by calling function for creating the QRCode
        binding.qrOutput.setImageBitmap(generateQRCode(userId))
    }

    private fun generateQRCode(inputText: String?): Bitmap? {
        val writer = MultiFormatWriter()
        var bitmap: Bitmap? = null

        if (!inputText.isNullOrEmpty()) {
            try {
                // init bit matrix
                val matrix = writer.encode(inputText, BarcodeFormat.QR_CODE, 350, 350)
                // init barcode encoder
                val encoder = BarcodeEncoder()
                // generate bitmap
                bitmap = encoder.createBitmap(matrix)
            } catch (e: WriterException) {
                // log error here
                Log.e("GENERATE QR CODE ACTIVITY", e.toString())
            }
        } else {
            //genQRBinding.textInputLayout.error = "* required"
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
        return bitmap
    }
}