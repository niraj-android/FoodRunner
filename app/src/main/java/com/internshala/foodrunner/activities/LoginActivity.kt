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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import org.json.JSONException
import org.json.JSONObject
import com.internshala.foodrunner.util.ConnectionManager

class LoginActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var txtForgotPassword: TextView
    lateinit var btnLogin: Button
    lateinit var txtRegisterYourself: TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_actvity)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        supportActionBar?.title = "Login"
        btnLogin.setOnClickListener {
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"
            if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("password", etPassword.text.toString())
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                if (success) {
                                    val data = data1.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", data.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", data.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", data.getString("email")).apply()


                                    sharedPreferences.edit().putString(
                                        "user_mobile_number",
                                        data.getString("mobile_number")
                                    ).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "user_address",
                                            data.getString("address")
                                        ).apply()

                                    savePreferences()
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()



                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Invalid Number/Password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Some unexpected error occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@LoginActivity,
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }

        }


        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, PasswordRecoveryActivity::class.java)
            startActivity(intent)

        }
        txtRegisterYourself.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }


    }
fun savePreferences(){
    sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()

}




    }
