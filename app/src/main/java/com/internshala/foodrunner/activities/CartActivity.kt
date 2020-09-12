package com.internshala.foodrunner.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.FtsOptions
import androidx.room.Query
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.Database.OrderDatabase
import com.internshala.foodrunner.Database.OrderEntity
import com.internshala.foodrunner.Database.RestaurantDatabase
import com.internshala.foodrunner.Database.RestaurantEntity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapters.CartRecyclerAdapter
import com.internshala.foodrunner.adapters.FavouriteRecyclerAdapter
import com.internshala.foodrunner.model.Cart
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var cartRecyclerAdapter: CartRecyclerAdapter
    lateinit var cartRecyclerView: RecyclerView
    lateinit var cartProgressBar: ProgressBar
    lateinit var cartProgressLayout: RelativeLayout
    lateinit var btnPlaceOrder: Button
    lateinit var empty:RelativeLayout
    lateinit var toolbar: Toolbar
    lateinit var txtOrderingFrom: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    var dbOrderList = arrayListOf<OrderEntity>()
    var cartDescriptionInfo= arrayListOf<Cart>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar = findViewById(R.id.toolbar)
        txtOrderingFrom = findViewById(R.id.txtOrderingFrom)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        cartProgressLayout = findViewById(R.id.cartProgressLayout)
        cartProgressBar = findViewById(R.id.cartProgressBar)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        sharedPreferences = getSharedPreferences("Login Preference", Context.MODE_PRIVATE)
        layoutManager = LinearLayoutManager(this@CartActivity)
        empty = findViewById(R.id.empty)
        empty.visibility = View.GONE
        cartProgressLayout.visibility = View.VISIBLE

        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurant_id")
            restaurantName = intent.getStringExtra("restaurant_name")
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val text = " Ordering from: $restaurantName"
        txtOrderingFrom.text = text
        dbOrderList = RetrieveOrder(this@CartActivity).execute().get() as ArrayList<OrderEntity>
        if (dbOrderList.isEmpty()) {
            cartProgressLayout.visibility = View.GONE
            empty.visibility = View.VISIBLE
            btnPlaceOrder.visibility=View.GONE
            txtOrderingFrom.visibility=View.GONE
        }else
        {

        for (i in 0 until dbOrderList.size) {
            var data = Cart(dbOrderList[i].foodName, dbOrderList[i].Price)
            cartDescriptionInfo.add(data)
            cartRecyclerAdapter = CartRecyclerAdapter(this@CartActivity, cartDescriptionInfo)
            cartRecyclerView.adapter = cartRecyclerAdapter
            cartRecyclerView.layoutManager = layoutManager
            cartRecyclerView.addItemDecoration(
                DividerItemDecoration(
                    cartRecyclerView.context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }



        cartProgressLayout.visibility = View.GONE
        var userId = sharedPreferences.getString("user_id", "")
        var totalCost = 0
        for (i in 0 until dbOrderList.size) {
            totalCost += dbOrderList[i].Price.toInt()
        }
        var text1 = "Place Order(Total Rs.$totalCost)"
        btnPlaceOrder.text = text1

        var foodJSONArray = JSONArray()
        for (i in 0 until dbOrderList.size) {
            var foodId = JSONObject()
            foodId.put("food_item_id", dbOrderList[i].foodId)
            foodJSONArray.put(i, foodId)
        }
        btnPlaceOrder.setOnClickListener {
            cartProgressLayout.visibility = View.VISIBLE
            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            if (ConnectionManager().checkConnectivity(this@CartActivity)) {

                val jsonParams = JSONObject()
                jsonParams.put("user_id", userId)
                jsonParams.put("restaurant_id", restaurantId)
                jsonParams.put("total_cost", totalCost.toString())
                jsonParams.put("food", foodJSONArray)
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(
                            Request.Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val data1 = it.getJSONObject("data")
                                    val success = data1.getBoolean("success")
                                    if (success) {
                                        cartProgressLayout.visibility = View.GONE
                                        DBAsyncTask(applicationContext, restaurantId, 1).execute()
                                            .get()


                                        val intent = Intent(
                                            this@CartActivity,
                                            OrderPlacedScreenActivity::class.java
                                        )
                                        startActivity(intent)

                                    } else {
                                        Toast.makeText(
                                            this@CartActivity,
                                            "Invalid Credentials!Retry",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@CartActivity,
                                        "Some unexpected error occurred!!!Retry",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@CartActivity,
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
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
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