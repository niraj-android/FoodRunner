package com.internshala.foodrunner.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
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

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var etOTP:EditText
    lateinit var etNum:EditText
    lateinit var etMatchNum:EditText
    lateinit var btnSubmit:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etOTP)
        etNum = findViewById(R.id.etNum)
        etMatchNum = findViewById(R.id.etMatchNum)
        btnSubmit=findViewById(R.id.btnSubmit)
        var number="9998886666"
        btnSubmit.setOnClickListener {
            if (etOTP.text.toString().isEmpty()) {
                Toast.makeText(this@ResetPasswordActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
            } else if (etNum.text.toString().isEmpty()) {
                Toast.makeText(this@ResetPasswordActivity, "Enter New Password", Toast.LENGTH_SHORT)
                    .show()
            } else if (etMatchNum.text.toString() != etNum.text.toString()) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Password didn't match",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", number)
                    jsonParams.put("password", etNum.text.toString())
                    jsonParams.put("otp", etOTP.text.toString())
                    val jsonObjectRequest =
                        object :
                            JsonObjectRequest(
                                Request.Method.POST, url, jsonParams, Response.Listener {
                                    try {
                                        val data1 = it.getJSONObject("data")
                                        val success = data1.getBoolean("success")
                                        if (success) {
                                            val successMessage = data1.getString("successMessage")
                                            val dialog =
                                                AlertDialog.Builder(this@ResetPasswordActivity)
                                            dialog.setTitle("Alert")
                                            dialog.setMessage(successMessage.toString())
                                            dialog.setPositiveButton("OK") { text, listener ->
                                                val intent = Intent(
                                                    this@ResetPasswordActivity,
                                                    LoginActivity::class.java
                                                )
                                                startActivity(intent)
                                                finish()

                                            }
                                            dialog.create()
                                            dialog.show()
                                        } else {
                                            Toast.makeText(
                                                this@ResetPasswordActivity,
                                                "Invalid Credentials!Retry",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: JSONException) {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            "Some unexpected error occurred!!!Retry",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                Response.ErrorListener {
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
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
                    val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setCancelable(false)
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            }

        }
    }
    }
