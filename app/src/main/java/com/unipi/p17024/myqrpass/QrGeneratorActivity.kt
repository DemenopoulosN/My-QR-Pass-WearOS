package com.unipi.p17024.myqrpass

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private lateinit var sharedPreferencesMain: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_generator)
        binding = ActivityQrGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity.sharedPreferencesMain = getSharedPreferences("sharedPreferencesMain", MODE_PRIVATE)

        val identifier = intent.getStringExtra("Identifier");
        if(identifier.equals("From_Activity_MainActivity")) {
            //reading token from Shared Preferences
            val tokenFromMain = sharedPreferencesMain.getString("token","default")
            //Toast.makeText(this, token, Toast.LENGTH_SHORT).show()

            //Updating imageView's image by calling function for creating the QRCode
            binding.qrOutput.setImageBitmap(generateQRCode(tokenFromMain))
        }
        else{
            databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smart-e-tickets-android-wearos-default-rtdb.firebaseio.com/")
            firebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = firebaseAuth.currentUser
            val phone = firebaseUser?.phoneNumber

            val userID = intent.getStringExtra("userID").toString()
            val myRef: DatabaseReference = databaseRef.child("Tokens").push()
            val token = myRef.key
            //Toast.makeText(this, token, Toast.LENGTH_SHORT).show()

            if (token != null) {
                databaseRef.child("Tokens").child(token).child("userID").setValue(userID)
                databaseRef.child("Tokens").child(token).child("timestamp").setValue(System.currentTimeMillis())

                databaseRef.child("Clients").child(userID).child("Valid Subscription").setValue("yes")
                databaseRef.child("Clients").child(userID).child("Personal Data").child("Name").setValue("")
                databaseRef.child("Clients").child(userID).child("Personal Data").child("Surname").setValue("")
                databaseRef.child("Clients").child(userID).child("Personal Data").child("Phone").setValue(phone)
            }


            //Updating imageView's image by calling function for creating the QRCode
            binding.qrOutput.setImageBitmap(generateQRCode(token))
        }

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