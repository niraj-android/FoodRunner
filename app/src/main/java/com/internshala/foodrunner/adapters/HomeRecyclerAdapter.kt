package com.internshala.foodrunner.adapters

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.Database.RestaurantDatabase
import com.internshala.foodrunner.Database.RestaurantEntity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activities.DescActivity
import com.internshala.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso
import java.util.ArrayList
import kotlinx.android.synthetic.main.home_single_row_recycler.view.*

class HomeRecyclerAdapter(val context:Context,val foodList: ArrayList<Restaurant>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeRecyclerAdapter.HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_single_row_recycler, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: HomeRecyclerAdapter.HomeViewHolder, position: Int) {
        val restaurant = foodList[position]

        holder.txtFoodName.text = restaurant.foodName
        holder.txtFoodRating.text = restaurant.foodRating
        holder.txtFoodPrice.text = restaurant.foodPrice

        Picasso.get().load(restaurant.foodImage).error(R.mipmap.ic_default_image)
            .into(holder.imgFoodImage)
        holder.homeContent.setOnClickListener {
            val intent= Intent(context,DescActivity::class.java)
            intent.putExtra("restaurant_id",restaurant.id)
            intent.putExtra("restaurant_name",restaurant.foodName)
            context.startActivity(intent)
        }

        val restaurantEntity = RestaurantEntity(
            restaurant.id,
            restaurant.foodName,
            restaurant.foodRating,
            restaurant.foodPrice,
            restaurant.foodImage
        )

        val checkfav = DBAsyncTask(context, restaurantEntity, 1).execute().get()
        if (checkfav) {
            holder.btnFavButton.setBackgroundResource(R.drawable.ic_action_fav)
        } else {
            holder.btnFavButton.setBackgroundResource(R.drawable.ic_action_unfav)
        }
        holder.btnFavButton.setOnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
                    holder.btnFavButton.setBackgroundResource(R.drawable.ic_action_fav)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show()
                    holder.btnFavButton.setBackgroundResource(R.drawable.ic_action_unfav)
                }

            }
        }

    }


    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnFavButton: Button = view.findViewById(R.id.btnFavButton)
        val txtFoodRating: TextView = view.findViewById(R.id.txtFoodRating)
        val homeContent: LinearLayout = view.findViewById(R.id.homeContent)
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
            .build()

        override fun doInBackground(vararg pO: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return book != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true


                }

            }

            return false
        }


    }
}
