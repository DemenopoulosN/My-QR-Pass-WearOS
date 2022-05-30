package com.unipi.p17024.myqrpass

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.unipi.p17024.myqrpass.databinding.ActivityPasscodeBinding


class PasscodeActivity : Activity() {
    private lateinit var binding: ActivityPasscodeBinding

    //private val sharedPreferences = this@PasscodeActivity.getPreferences(Context.MODE_PRIVATE)
    //var context: Context? = this@PasscodeActivity

    //sharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode)
        binding = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing sharedPreferences
        sharedPreferences = getPreferences(MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.remove("passcode")
        editor.apply()
        val passcode = sharedPreferences.getInt("passcode", 0)
        if(passcode != 0){
            binding.textTitle.isVisible = false
            binding.textTitle3.isVisible = true
            binding.editTextNumberPassword1.isVisible = false
            binding.editTextNumberPassword2.isVisible = false
            binding.editTextNumberPassword3.isVisible = false
            binding.editTextNumberPassword4.isVisible = false
            binding.editTextNumberPin1.isVisible = true
            binding.editTextNumberPin2.isVisible = true
            binding.editTextNumberPin3.isVisible = true
            binding.editTextNumberPin4.isVisible = true
            binding.buttonContinue.isVisible = false
            binding.buttonContinue3.isVisible = true
        }
        //

        //Continue Button onclick: input phone number, validate, start phone authentication/login
        binding.buttonContinue.setOnClickListener {
            if(binding.editTextNumberPassword1.text.isEmpty() || binding.editTextNumberPassword2.text.isEmpty() || binding.editTextNumberPassword3.text.isEmpty() || binding.editTextNumberPassword4.text.isEmpty()){
                Toast.makeText(this, "Please enter a correct PIN",Toast.LENGTH_SHORT).show()
            }
            else{
                binding.textTitle.isVisible = false
                binding.textTitle2.isVisible = true
                binding.editTextNumberPassword1.isVisible = false
                binding.editTextNumberPassword2.isVisible = false
                binding.editTextNumberPassword3.isVisible = false
                binding.editTextNumberPassword4.isVisible = false
                binding.editTextNumberConfirm1.isVisible = true
                binding.editTextNumberConfirm2.isVisible = true
                binding.editTextNumberConfirm3.isVisible = true
                binding.editTextNumberConfirm4.isVisible = true
                binding.buttonContinue.isVisible = false
                binding.buttonContinue2.isVisible = true
            }
        }

        binding.buttonContinue2.setOnClickListener {
            if(binding.editTextNumberConfirm1.text.isEmpty() || binding.editTextNumberConfirm2.text.isEmpty() || binding.editTextNumberConfirm3.text.isEmpty() || binding.editTextNumberConfirm4.text.isEmpty()){
                Toast.makeText(this, "Please enter a correct PIN",Toast.LENGTH_SHORT).show()
            }
            else if(binding.editTextNumberConfirm1.text.toString() != binding.editTextNumberPassword1.text.toString() || binding.editTextNumberConfirm2.text.toString() != binding.editTextNumberPassword2.text.toString() || binding.editTextNumberConfirm3.text.toString() != binding.editTextNumberPassword3.text.toString() || binding.editTextNumberConfirm4.text.toString() != binding.editTextNumberPassword4.text.toString()){
                Toast.makeText(this, "PIN not correct",Toast.LENGTH_SHORT).show()
            }
            else{
                //val editor = sharedPreferences.edit()
                val pin: Int = Integer.valueOf(binding.editTextNumberConfirm1.text.toString()+binding.editTextNumberConfirm2.text.toString()+binding.editTextNumberConfirm3.text.toString()+binding.editTextNumberConfirm4.text.toString())
                //Toast.makeText(this, pin.toString(), Toast.LENGTH_SHORT).show()
                editor.putInt("passcode", pin)
                editor.apply()

                //starting MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.buttonContinue3.setOnClickListener {
            if(binding.editTextNumberPin1.text.isEmpty() || binding.editTextNumberPin2.text.isEmpty() || binding.editTextNumberPin3.text.isEmpty() || binding.editTextNumberPin4.text.isEmpty()){
                Toast.makeText(this, "Please enter a correct PIN",Toast.LENGTH_SHORT).show()
            }
            else if(passcode != Integer.valueOf(binding.editTextNumberPin1.text.toString()+binding.editTextNumberPin2.text.toString()+binding.editTextNumberPin3.text.toString()+binding.editTextNumberPin4.text.toString())){
                Toast.makeText(this, "PIN not correct",Toast.LENGTH_SHORT).show()
            }
            else{
                //starting QrGeneratorActivity
                val intent = Intent(this, QrGeneratorActivity::class.java)
                startActivity(intent)
            }
        }

        //
        // Moving to next editText quickly
        //
        binding.editTextNumberPassword1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberPassword1.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberPassword1.hideKeyboard()
                }
            }
        })

        binding.editTextNumberPassword2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberPassword2.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberPassword2.hideKeyboard()
                }
            }
        })

        binding.editTextNumberPassword3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberPassword3.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberPassword3.hideKeyboard()
                }
            }
        })

        binding.editTextNumberPassword4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberPassword4.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberPassword4.hideKeyboard()
                }
            }
        })

        binding.editTextNumberConfirm1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberConfirm1.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberConfirm1.hideKeyboard()
                }
            }
        })

        binding.editTextNumberConfirm2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberConfirm2.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberConfirm2.hideKeyboard()
                }
            }
        })

        binding.editTextNumberConfirm3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberConfirm3.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberConfirm3.hideKeyboard()
                }
            }
        })

        binding.editTextNumberConfirm4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberConfirm4.text.toString().trim().isNotEmpty()) {
                    binding.editTextNumberConfirm4.hideKeyboard()
                }
            }
        })
    }

    fun temporary(){
        //write data to sharedPreferences
        val editor = sharedPreferences.edit()
        editor.putInt("passcode", 1234)
        editor.apply()


        //read data from sharedPreferences
        val passcode = sharedPreferences.getInt("passcode", 0)
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}