package com.internshala.foodrunner.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.internshala.foodrunner.R


class UserProfileFragment : Fragment() {
     var sharedPreferences: SharedPreferences?=null
    lateinit var txtName:TextView
    lateinit var txtNumber:TextView
    lateinit var txtEmail:TextView
    lateinit var txtAddress:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_user_profile, container, false)
txtName=view.findViewById(R.id.txtName)
        txtNumber=view.findViewById(R.id.txtNumber)
        txtEmail=view.findViewById(R.id.txtEmail)
        txtAddress=view.findViewById(R.id.txtAddress)
        sharedPreferences=this.activity?.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
var name="${sharedPreferences?.getString("user_name","")}"
        txtName.text=name
        var number="+91-${sharedPreferences?.getString("user_mobile_number","")}"
        txtNumber.text=number
        var email="${sharedPreferences?.getString("user_email","")}"
        txtEmail.text=email
        var address="${sharedPreferences?.getString("user_address","")}"
        txtAddress.text=address
        return view
    }


}