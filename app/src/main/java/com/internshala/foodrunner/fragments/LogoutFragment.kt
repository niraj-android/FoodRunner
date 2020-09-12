package com.internshala.foodrunner.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activities.LoginActivity
import com.internshala.foodrunner.activities.MainActivity


class LogoutFragment : Fragment() {
    var sharedPreferences: SharedPreferences?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
val view=inflater.inflate(R.layout.fragment_logout, container, false)

        val dialog= AlertDialog.Builder(activity as Context)
        dialog.setTitle("Confirmation")
        dialog.setCancelable(false)
        dialog.setMessage("Are you sure you want to logout?")
        dialog.setPositiveButton("No"){
                text,listener->
            val intent=Intent(context, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        dialog.setNegativeButton("Yes"){
                text,listener->
            sharedPreferences=this.activity?.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.clear()?.apply()
            val intent=Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }
        dialog.create()
        dialog.show()


        return view
    }



}