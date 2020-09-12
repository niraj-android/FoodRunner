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

class PasswordRecoveryActivity : AppCompatActivity() {
    lateinit var etForgotMobile:EditText
    lateinit var etForgotEmail:EditText
    lateinit var btnForgotNext:Button
    lateinit var toolbar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Password Recovery"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        etForgotMobile=findViewById(R.id.etForgotMobile)
        etForgotEmail=findViewById(R.id.etForgotEmail)

        btnForgotNext=findViewById(R.id.btnForgotNext)
        btnForgotNext.setOnClickListener {
            if (etForgotMobile.text.toString().isEmpty()) {
                Toast.makeText(
                    this@PasswordRecoveryActivity,
                    "Enter Mobile Number",
                    Toast.LENGTH_SHORT).show()

            }else if(etForgotEmail.text.toString().isEmpty()) {
                Toast.makeText(
                    this@PasswordRecoveryActivity,
                    "Enter Email",
                    Toast.LENGTH_SHORT).show()

            } else {
            if (ConnectionManager().checkConnectivity(this@PasswordRecoveryActivity)) {
                val queue = Volley.newRequestQueue(this@PasswordRecoveryActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result "
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etForgotMobile.text.toString())
                jsonParams.put("email", etForgotEmail.text.toString())
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(
                            Request.Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {
                                        val first_try = data.getBoolean("first_try")
                                        if (first_try) {
                                            Toast.makeText(
                                                this@PasswordRecoveryActivity,
                                                "sending....",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val dialog =
                                                AlertDialog.Builder(this@PasswordRecoveryActivity)
                                            dialog.setTitle("Alert")
                                            dialog.setMessage("OTP sent to your email.Please check your email")
                                            dialog.setPositiveButton("Ok") { text, listener ->
                                                val intent=Intent(this@PasswordRecoveryActivity,
                                                    ResetPasswordActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }

                                            dialog.create()
                                            dialog.show()
                                        } else {
                                            val dialog =
                                                AlertDialog.Builder(this@PasswordRecoveryActivity)
                                            dialog.setTitle("Alert")
                                            dialog.setMessage("OTP has been sent to your email.Please check your email")
                                            dialog.setPositiveButton("Ok") { text, listener ->
                                                val intent = Intent(
                                                    this@PasswordRecoveryActivity,
                                                    ResetPasswordActivity::class.java
                                                )
                                                startActivity(intent)
                                                finish()
                                            }

                                            dialog.create()
                                            dialog.show()



                                        }

                                    }else {
                                        Toast.makeText(
                                            this@PasswordRecoveryActivity,
                                            "Invalid Credentials!Re-enter",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@PasswordRecoveryActivity,
                                        "Some error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@PasswordRecoveryActivity,
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
                val dialog = AlertDialog.Builder(this@PasswordRecoveryActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@PasswordRecoveryActivity)
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


}