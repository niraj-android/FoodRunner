package com.internshala.foodrunner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.model.Cart
import kotlinx.android.synthetic.main.cart_single_row_recycler.view.*

class CartRecyclerAdapter(val context: Context,val cartList:ArrayList<Cart>):RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> (){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartRecyclerAdapter.CartViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_single_row_recycler, parent, false)
        return CartRecyclerAdapter.CartViewHolder(view)

    }

    override fun onBindViewHolder(holder: CartRecyclerAdapter.CartViewHolder, position: Int) {
        val data=cartList[position]
        holder.txtFoodName.text=data.foodName
        holder.txtFoodCost.text=data.foodPrice
    }

    override fun getItemCount(): Int {
        return cartList.size
    }
    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtFoodName:TextView=view.findViewById(R.id.txtFoodName)
        val txtFoodCost:TextView=view.findViewById(R.id.txtFoodCost)

    }
}