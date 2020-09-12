package com.internshala.foodrunner.adapters

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.Database.RestaurantDatabase
import com.internshala.foodrunner.Database.RestaurantEntity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activities.DescActivity
import com.internshala.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso
import java.util.ArrayList

class FavouriteRecyclerAdapter (val context: Context, val foodList: ArrayList<RestaurantEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouritesViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteRecyclerAdapter.FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favourite_single_row_recycler, parent, false)
        return FavouriteRecyclerAdapter.FavouritesViewHolder(view)


    }

    override fun onBindViewHolder(
        holder: FavouriteRecyclerAdapter.FavouritesViewHolder,
        position: Int
    ) {
        val restaurant = foodList[position]

        holder.txtFoodName.text = restaurant.foodName
        holder.txtFoodRating.text = restaurant.foodRating
        holder.txtFoodPrice.text = restaurant.foodPrice

        Picasso.get().load(restaurant.foodImage).error(R.mipmap.ic_default_image)
            .into(holder.imgFoodImage)
        holder.favContent.setOnClickListener {
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
        holder.btnFavButton.setOnClickListener {

             DBAsyncTask(context, restaurantEntity, 3).execute().get()
            Toast.makeText(context,"Removed from favourites",Toast.LENGTH_SHORT).show()

            DBAsyncTask(context,restaurantEntity,4).execute().get()


        }

    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    class FavouritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnFavButton: Button = view.findViewById(R.id.btnFavButton)
        val txtFoodRating: TextView = view.findViewById(R.id.txtFoodRating)
        val favContent: LinearLayout = view.findViewById(R.id.favContent)
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
                4->{db.restaurantDao().getAllRestaurant()
                db.close()
                return true}

            }

            return false
        }


    }
}