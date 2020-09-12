package com.internshala.foodrunner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.model.Cart
import com.internshala.foodrunner.model.OrderHistory



class OrderHistoryRecyclerAdapter(context: Context, val orderList: ArrayList<OrderHistory>): RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder> () {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryRecyclerAdapter.OrderHistoryViewHolder {
       val view=LayoutInflater.from(parent.context)
           .inflate(R.layout.orderhistory_single_row, parent, false)
        return OrderHistoryRecyclerAdapter.OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: OrderHistoryRecyclerAdapter.OrderHistoryViewHolder,
        position: Int
    ) {
        val orderHistory = orderList[position]
        holder.txtRestaurantName.text = orderHistory.restaurantName
        holder.txtOrderDate.text = orderHistory.orderDate
        setUpRecycler(holder.orderHistoryRecycler,orderHistory,holder.itemView.context)

    }
    private fun setUpRecycler(recyclerView: RecyclerView, orderList: OrderHistory,context:Context) {
        val foodItems = java.util.ArrayList<Cart>()
        for (i in 0 until orderList.foodItem.length()) {
            val foodJson = orderList.foodItem.getJSONObject(i)
            foodItems.add(
                Cart(
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val cartItemAdapter = CartRecyclerAdapter(context,foodItems)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = cartItemAdapter


    }
    override fun getItemCount(): Int {
      return orderList.size
    }
    class OrderHistoryViewHolder(view: View):RecyclerView.ViewHolder(view){
var txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        var txtOrderDate:TextView=view.findViewById(R.id.txtOrderDate)
        var orderHistoryRecycler:RecyclerView=view.findViewById(R.id.orderHistoryRecycler)

    }
}