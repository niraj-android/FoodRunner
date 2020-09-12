package com.internshala.foodrunner.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapters.HomeRecyclerAdapter
import com.internshala.foodrunner.adapters.OrderHistoryRecyclerAdapter
import com.internshala.foodrunner.model.OrderHistory
import com.internshala.foodrunner.model.Restaurant
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONException


class OrderHistoryFragment : Fragment() {
    lateinit var orderHistoryRecycler: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var orderHistoryRecyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var orderHistoryProgressLayout:RelativeLayout
    lateinit var orderHistoryProgressBar: ProgressBar
    lateinit var empty:RelativeLayout
     var sharedPreferences: SharedPreferences?=null
    val orderInfoList= arrayListOf<OrderHistory>()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view=inflater.inflate(R.layout.fragment_order_history, container, false)
        orderHistoryRecycler=view.findViewById(R.id.orderHistoryRecycler)
        layoutManager= LinearLayoutManager(activity)
        orderHistoryProgressLayout=view.findViewById(R.id.orderHistoryProgressLayout)
        orderHistoryProgressBar=view.findViewById(R.id.orderHistoryProgressBar)
        empty=view.findViewById(R.id.empty)
        empty.visibility=View.GONE

        sharedPreferences=this.activity?.getSharedPreferences("Login Preference",Context.MODE_PRIVATE)
        var userId:String = sharedPreferences?.getString("user_id",null).toString()

        orderHistoryProgressLayout.visibility=View.VISIBLE
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url,null, Response.Listener {

                    try {
                        orderHistoryProgressLayout.visibility = View.GONE
                        val data=it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            val data1 = data.getJSONArray("data")
                            if (data1.length() == 0) {
                                empty.visibility = View.VISIBLE
                            } else
                            {
                                for (i in 0 until data1.length()) {
                                    val orderJsonObject = data1.getJSONObject(i)
                                    var foodItem = orderJsonObject.getJSONArray("food_items")
                                    val orderObject = OrderHistory(
                                        orderJsonObject.getString("order_id"),
                                        orderJsonObject.getString("restaurant_name"),
                                        orderJsonObject.getString("order_placed_at"),
                                        foodItem

                                    )

                                    orderInfoList.add(orderObject)
                                    orderHistoryRecyclerAdapter =
                                        OrderHistoryRecyclerAdapter(
                                            activity as Context,
                                            orderInfoList
                                        )
                                    orderHistoryRecycler.adapter = orderHistoryRecyclerAdapter
                                    orderHistoryRecycler.layoutManager = layoutManager
                                    orderHistoryRecycler.addItemDecoration(
                                        DividerItemDecoration(
                                            orderHistoryRecycler.context,
                                            (layoutManager as LinearLayoutManager).orientation
                                        )
                                    )


                                }
                        } } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        activity as Context,
                        "volley error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                {
                    override fun getHeaders(): MutableMap<String, String> {

                        val headers=HashMap<String,String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="9bf534118365f1"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)


        }else{
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Open Settings"){
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){
                    text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }




        return view
    }

}