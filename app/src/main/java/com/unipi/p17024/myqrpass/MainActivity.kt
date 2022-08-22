package com.unipi.p17024.myqrpass

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.phone.SmsRetrieverApi
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.unipi.p17024.myqrpass.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : Activity(){

    private lateinit var binding: ActivityMainBinding

    //if code sending failed resend code
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    private val TAG = "MAIN_TAG"

    //progress dialog
    private  lateinit var progressDialog: ProgressDialog

    private lateinit var sharedPreferencesPasscode: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smart-e-tickets-android-wearos-default-rtdb.firebaseio.com/")

        //see companion object -> line 216
        sharedPreferencesMain = getSharedPreferences("sharedPreferencesMain", MODE_PRIVATE)

        //initializing sharedPreferences
        sharedPreferencesPasscode = getSharedPreferences("sharedPreferencesPasscode", MODE_PRIVATE)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                //Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Log.d(TAG,"onVerificationFailed: ${e.message}")
                //Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                progressDialog.dismiss()
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                Log.d(TAG, "onCodeSent: $verificationId")

                //set views to VISIBLE-INVISIBLE
                Toast.makeText(this@MainActivity, "Verification code sent!", Toast.LENGTH_SHORT).show()
                binding.text3.text = "Enter the code we sent to ${binding.editTextPhone.text.toString().trim()}"
            }
        }

        //Continue Button onclick: input phone number, validate, start phone authentication/login
        binding.buttonPhone.setOnClickListener {
            //input phone number
            val phone = binding.editTextPhone.text.toString().trim()
            //validate phone number
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity, "Please enter a phone number first", Toast.LENGTH_SHORT).show()
            }
            else{
                binding.buttonPhone.isVisible = false;
                binding.editTextPhone.isVisible = false;
                binding.text2.isVisible = false;
                binding.editTextOTP.isVisible = true;
                binding.text3.isVisible = true;
                binding.buttonPhone2.isVisible = true;
                binding.text4.isVisible = true;
                binding.text5.isVisible = true;

                // Turn off phone auth app verification for testing (EMULATOR)
                FirebaseAuth.getInstance().firebaseAuthSettings.setAppVerificationDisabledForTesting(true)

                startPhoneNumberVerification(phone)
            }
        }

        //resendCode button onclick: (if code wasn't received) resend otp
        binding.text4.setOnClickListener {
            //resend phone number
            val phone = binding.editTextPhone.text.toString().trim()
            //validate phone number
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity, "Please enter a phone number first", Toast.LENGTH_SHORT).show()
            }
            else{
                resendVerificationCode(phone, forceResendingToken)
            }
        }

        //submit button onclick: input verification code, validate, verify phone number with verification code
        binding.buttonPhone2.setOnClickListener {
            //input verification code
            val code = binding.editTextOTP.text.toString().trim()
            if(TextUtils.isEmpty(code)){
                Toast.makeText(this@MainActivity, "Please enter the code sent to your phone", Toast.LENGTH_SHORT).show()
            }
            else{
                verifyPhoneNumberWithCode(mVerificationId, code)
            }
        }
    }

    private fun startPhoneNumberVerification(phone: String){
        Log.d(TAG, "startPhoneNumberVerification: $phone")
        progressDialog.setMessage("Verifying phone number...")
        progressDialog.show()

        val options = mCallbacks?.let {
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(it)
                .build()
        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken?){
        progressDialog.setMessage("Resending code...")
        progressDialog.show()

        Log.d(TAG, "resendVerificationCode: $phone")

        val options = mCallbacks?.let {
            token?.let { it1 ->
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(it)
                    .setForceResendingToken(it1)
                    .build()
            }
        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code:String){
        Log.d(TAG, "verifyPhoneNumberWithCode: $verificationId $code")
        progressDialog.setMessage("Verifying code...")
        progressDialog.show()

        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, code) }
        if (credential != null) {
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: $credential")
        progressDialog.setMessage("Logging in")
        progressDialog.show()

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(this, "Logged in as $phone", Toast.LENGTH_SHORT).show()

                val firebaseUser = firebaseAuth.currentUser
                val userID = firebaseUser?.uid

                //putting user's data into database
                if (userID != null) {
                    val myRef: DatabaseReference = databaseRef.child("Tokens").push()
                    val token = myRef.key

                    //saving token to Shared Preferences(+ userID & timestamp for passcode activity)
                    val editor = sharedPreferencesMain.edit()
                    val editor2 = sharedPreferencesPasscode.edit()
                    editor.putString("token", token)
                    editor2.putString("userID",userID)
                    editor2.putLong("timestamp",System.currentTimeMillis())
                    editor.apply()
                    editor2.apply()


                    if (token != null) {
                        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(!snapshot.child("Clients").child(userID).exists()){
                                    databaseRef.child("Tokens").child(token).child("userID").setValue(userID)
                                    databaseRef.child("Tokens").child(token).child("timestamp").setValue(System.currentTimeMillis())

                                    databaseRef.child("Clients").child(userID).child("Valid Subscription").setValue("yes")
                                    databaseRef.child("Clients").child(userID).child("Personal Data").child("Name").setValue("")
                                    databaseRef.child("Clients").child(userID).child("Personal Data").child("Surname").setValue("")
                                    databaseRef.child("Clients").child(userID).child("Personal Data").child("Phone").setValue(phone)
                                }
                                else{
                                    databaseRef.child("Tokens").child(token).child("userID").setValue(userID)
                                    databaseRef.child("Tokens").child(token).child("timestamp").setValue(System.currentTimeMillis())
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                            }
                        })
                    }
                }

                //starting QrGeneratorActivity
                val intent = Intent(this, QrGeneratorActivity::class.java)
                intent.putExtra("Identifier", "From_Activity_MainActivity")
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        //sharedPreferences
        lateinit var sharedPreferencesMain: SharedPreferences
    }
}