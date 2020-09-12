package com.internshala.foodrunner.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.Database.RestaurantDatabase
import com.internshala.foodrunner.Database.RestaurantEntity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapters.HomeRecyclerAdapter
import com.internshala.foodrunner.model.Restaurant
import org.json.JSONException
import com.internshala.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.favourite_single_row_recycler.*
import java.util.*
import kotlin.collections.HashMap



class HomeFragment : Fragment() {

 val foodInfoList= arrayListOf<Restaurant>()
    lateinit var homeRecyclerView: RecyclerView
    lateinit var homeProgressLayout: RelativeLayout
    lateinit var homeProgressBar: ProgressBar
    lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    var ratingComparator= Comparator<Restaurant>{restaurant1,restaurant2->
        if(restaurant1.foodRating.compareTo(restaurant2.foodRating,true)==0)
        {
            restaurant1.foodName.compareTo(restaurant2.foodName,true)
        }
        else{
            restaurant1.foodRating.compareTo(restaurant2.foodRating,true)
        }
    }

    var costComparator= Comparator<Restaurant> { rest1, rest2 ->

        if (rest1.foodPrice.compareTo(rest2.foodPrice, true) == 0) {
            rest1.foodName.compareTo(rest2.foodName, true)
        } else {
            rest1.foodPrice.compareTo(rest2.foodPrice, true)
        }
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeRecyclerView = view.findViewById(R.id.homeRecyclerView)
       homeProgressBar = view.findViewById(R.id.homeProgressBar)
        homeProgressLayout = view.findViewById(R.id.homeProgressLayout)
        homeProgressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)


        setHasOptionsMenu(true)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
if(ConnectionManager().checkConnectivity(activity as Context)){
    val jsonObjectRequest =
        object : JsonObjectRequest(Request.Method.GET, url,null, Response.Listener {

            try {
               homeProgressLayout.visibility = View.GONE
                val data1=it.getJSONObject("data")
                val success = data1.getBoolean("success")
                if (success) {
                    val data = data1.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val restaurantJsonObject = data.getJSONObject(i)
                        val restaurantObject = Restaurant(
                            restaurantJsonObject.getString("id"),
                            restaurantJsonObject.getString("name"),
                            restaurantJsonObject.getString("rating"),
                            restaurantJsonObject.getString("cost_for_one"),
                            restaurantJsonObject.getString("image_url")
                        )

                        foodInfoList.add(restaurantObject)
                        homeRecyclerAdapter=
                            HomeRecyclerAdapter(activity as Context,foodInfoList)
                        homeRecyclerView.adapter=homeRecyclerAdapter
                        homeRecyclerView.layoutManager=layoutManager
                        homeRecyclerView.addItemDecoration(
                            DividerItemDecoration(
                                homeRecyclerView.context,
                                (layoutManager as LinearLayoutManager).orientation
                            )
                        )


                    }
                } else {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_dashboard,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.sortByRating){
            Collections.sort(foodInfoList,ratingComparator)
                foodInfoList.reverse()
            homeRecyclerAdapter.notifyDataSetChanged()
        }else{
            if (id==R.id.sortByCost){
                Collections.sort(foodInfoList,costComparator)
                foodInfoList.reverse()
                homeRecyclerAdapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    }



