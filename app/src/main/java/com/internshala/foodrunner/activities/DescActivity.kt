package com.internshala.foodrunner.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.Database.OrderDatabase
import com.internshala.foodrunner.Database.OrderEntity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapters.DescriptionRecyclerAdapter
import com.internshala.foodrunner.adapters.HomeRecyclerAdapter
import com.internshala.foodrunner.model.Description
import com.internshala.foodrunner.model.Restaurant
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONException

class DescActivity : AppCompatActivity() {
    lateinit var descRecyclerView: RecyclerView
    lateinit var descProgressLayout:RelativeLayout
    lateinit var descProgressBar: ProgressBar
    lateinit var btnAddCart:Button
    lateinit var descriptionRecyclerAdapter: DescriptionRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var restaurant_name:String
    lateinit var toolbar:Toolbar
    lateinit var restaurantId:String
    var dbOrderList= arrayListOf<OrderEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desc)
        descProgressLayout=findViewById(R.id.descProgressLayout)
        descProgressBar=findViewById(R.id.descProgressBar)
        descRecyclerView=findViewById(R.id.descRecycler)
        btnAddCart=findViewById(R.id.btnAddCart)
        toolbar=findViewById(R.id.toolbar)
        layoutManager=LinearLayoutManager(this@DescActivity)
        descProgressLayout.visibility= View.VISIBLE

        val descriptionInfo= arrayListOf<Description>()

        if (intent!=null){
                restaurantId = intent.getStringExtra("restaurant_id")
            restaurant_name=intent.getStringExtra("restaurant_name")

        }
setSupportActionBar(toolbar)
        supportActionBar?.title=restaurant_name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dbOrderList = CartActivity.RetrieveOrder(this@DescActivity).execute().get() as ArrayList<OrderEntity>


            btnAddCart.setOnClickListener {

                val intent = Intent(this@DescActivity, CartActivity::class.java)
                intent.putExtra("restaurant_id", restaurantId)
                intent.putExtra("restaurant_name", restaurant_name)
                startActivity(intent)
                finish()
            }

        val queue = Volley.newRequestQueue(this@DescActivity)
        val url = " http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId "
        if(ConnectionManager().checkConnectivity(this@DescActivity)){
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        descProgressLayout.visibility = View.GONE
                        val data1=it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success) {
                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Description(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("restaurant_id")

                                )

                                descriptionInfo.add(restaurantObject)
                                descriptionRecyclerAdapter=
                                    DescriptionRecyclerAdapter(this@DescActivity,descriptionInfo)
                                descRecyclerView.adapter=descriptionRecyclerAdapter
                                descRecyclerView.layoutManager=layoutManager
                                descRecyclerView.addItemDecoration(
                                    DividerItemDecoration(
                                        descRecyclerView.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )


                            }
                        } else {
                            Toast.makeText(
                                this@DescActivity,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DescActivity,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@DescActivity,
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
            val dialog= AlertDialog.Builder(this@DescActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Open Settings"){
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){
                    text,listener->
                ActivityCompat.finishAffinity(this@DescActivity)
            }
            dialog.create()
            dialog.show()
        }





    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    class RetrieveOrder(val context: Context) :
        AsyncTask<Void, Void, List<OrderEntity>>() {
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            val db =
                Room.databaseBuilder(context, OrderDatabase::class.java, "orders-db").build()
            return db.orderDao().getAllOrders()
        }
    }
    class DBAsyncTask(val context: Context, val id:String, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "orders-db")
            .build()

        override fun doInBackground(vararg pO: Void?): Boolean {

            when (mode) {

                1 -> {
                    db.orderDao().deleteOrderById(id)
                    db.close()
                    return true
                }
            }

            return false
        }


    }


    override fun onBackPressed() {
        DBAsyncTask(applicationContext,restaurantId,1).execute().get()
        super.onBackPressed()
    }
}