package com.internshala.foodrunner.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import org.json.JSONException
import org.json.JSONObject
import com.internshala.foodrunner.util.ConnectionManager

class RegisterActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etAddress: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var toolbar:Toolbar
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnRegister = findViewById(R.id.btnRegister)
        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isRegisteredIn = sharedPreferences.getBoolean("isRegistered", false)
        if (isRegisteredIn) {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



        btnRegister.setOnClickListener {
            if (etName.text.toString().isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (etEmail.text.toString().isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Enter Email", Toast.LENGTH_SHORT).show()
            } else if (etAddress.text.toString().isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Enter Address", Toast.LENGTH_SHORT).show()
            } else if (etPhoneNumber.text.toString().isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Enter Mobile Number", Toast.LENGTH_SHORT)
                    .show()
            } else if (etPassword.text.toString().isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Enter Password ", Toast.LENGTH_SHORT).show()
            } else if (etPassword.text.toString().isEmpty() != etConfirmPassword.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password doesn't match.Please Re-enter",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                    val queue = Volley.newRequestQueue(this@RegisterActivity)
                    val url = "http://13.235.250.119/v2/register/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etPhoneNumber.text.toString())
                    jsonParams.put("password", etPassword.text.toString())
                    jsonParams.put("name", etName.text.toString())
                    jsonParams.put("address", etAddress.text.toString())
                    jsonParams.put("email", etEmail.text.toString())
                    val jsonObjectRequest =
                        object :
                            JsonObjectRequest(
                                Request.Method.POST, url, jsonParams, Response.Listener {
                                    try {
                                        val data1 = it.getJSONObject("data")
                                        val success = data1.getBoolean("success")
                                        if (success) {
                                            val data = data1.getJSONObject("data")
                                            sharedPreferences.edit()
                                                .putString("user_id", data.getString("user_id"))
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString("user_name", data.getString("name"))
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString("user_email", data.getString("email"))
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString(
                                                    "user_mobile_number",
                                                    data.getString("mobile_number")
                                                ).apply()
                                            sharedPreferences.edit()
                                                .putString(
                                                    "user_address",
                                                    data.getString("address")
                                                ).apply()
                                            savePreferences()
                                            Toast.makeText(this@RegisterActivity,"Registration successful",Toast.LENGTH_SHORT).show()

                                            val intent = Intent(
                                                this@RegisterActivity,
                                                LoginActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()


                                        } else {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "Incorrect credentials",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: JSONException) {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Some unexpected error occurred!!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                Response.ErrorListener {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "volley error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                            override fun getHeaders(): MutableMap<String, String> {

                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "9bf534118365f1"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)

                } else {
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setCancelable(false)
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            }
        }


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun savePreferences() {
        sharedPreferences.edit().putBoolean("isRegisteredIn", true).apply()

    }
}


