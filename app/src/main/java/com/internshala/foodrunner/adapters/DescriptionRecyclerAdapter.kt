package com.internshala.foodrunner.adapters

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.internshala.foodrunner.Database.OrderDatabase
import com.internshala.foodrunner.Database.OrderEntity
import com.internshala.foodrunner.Database.RestaurantDatabase
import com.internshala.foodrunner.Database.RestaurantEntity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.model.Description
import kotlinx.android.synthetic.main.description_single_row_recycler.view.*
import org.w3c.dom.Text

class DescriptionRecyclerAdapter(val context: Context,val descriptionList: ArrayList<Description>):RecyclerView.Adapter<DescriptionRecyclerAdapter.DescriptionViewHolder> (){






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.description_single_row_recycler, parent, false)
        return DescriptionRecyclerAdapter.DescriptionViewHolder(view)

    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        val data =descriptionList[position]
        val orderEntity=OrderEntity(data.id,
        data.foodName,
        data.foodPrice,
        data.restaurantId)
        holder.txtSerialNo.text=(position+1).toString()
            holder.txtItemName.text=data.foodName
            holder.txtCost.text=data.foodPrice
        val check=DBAsyncTask(context,orderEntity,1).execute().get()
        if(check){
            val color=ContextCompat.getColor(context,R.color.colorPrimary)
            holder.btnAdd.setBackgroundColor(color)
            holder.btnAdd.text="Remove"

        }else{val color=ContextCompat.getColor(context,R.color.colorAccent)
            holder.btnAdd.setBackgroundColor(color)
            holder.btnAdd.text="Add"

        }
            holder.btnAdd.setOnClickListener {
                if (!DBAsyncTask(context,orderEntity,1).execute().get()){
                    val async=DBAsyncTask(context,orderEntity,2).execute().get()
                    if (async){
                        val color=ContextCompat.getColor(context,R.color.colorPrimary)
                        holder.btnAdd.setBackgroundColor(color)
                        holder.btnAdd.text="Remove"
                    }else{
                        val color=ContextCompat.getColor(context,R.color.colorAccent)
                        holder.btnAdd.setBackgroundColor(color)
                        holder.btnAdd.text="Add"
                    }
                }else{
                    val async=DBAsyncTask(context,orderEntity,3).execute().get()
                    if(async){
                        val color=ContextCompat.getColor(context,R.color.colorAccent)
                        holder.btnAdd.setBackgroundColor(color)
                        holder.btnAdd.text="Add"
                    }
                }

            }
    }

    override fun getItemCount(): Int {
        return  descriptionList.size
    }
    class DescriptionViewHolder(view:View): RecyclerView.ViewHolder(view){
        var txtSerialNo:TextView=view.findViewById(R.id.txtSerialNo)
        val txtItemName:TextView=view.findViewById(R.id.txtItemName)
        val txtCost:TextView=view.findViewById(R.id.txtCost)
        val btnAdd:Button=view.findViewById(R.id.btnAdd)

    }
    class DBAsyncTask(val context: Context, val orderEntity: OrderEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = databaseBuilder(context, OrderDatabase::class.java, "orders-db")
            .build()

        override fun doInBackground(vararg pO: Void?): Boolean {

            when (mode) {
                1 -> {
                    val order: OrderEntity? =
                        db.orderDao().getOrderById(orderEntity.foodId.toString())
                    db.close()
                    return order != null
                }
                2 -> {
                    db.orderDao().insertOrder(orderEntity)
                    db.close()
                    return true

                }
                3 -> {
                    db.orderDao().deleteOrder(orderEntity)
                    db.close()
                    return true


                }

            }

            return false
        }


    }
}