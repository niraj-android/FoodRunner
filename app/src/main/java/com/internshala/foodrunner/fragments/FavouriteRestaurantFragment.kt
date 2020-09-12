package com.internshala.foodrunner.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.Database.RestaurantDatabase
import com.internshala.foodrunner.Database.RestaurantEntity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapters.FavouriteRecyclerAdapter
import kotlinx.android.synthetic.main.favourite_single_row_recycler.*


class FavouriteRestaurantFragment : Fragment() {
    lateinit var favouriteRecyclerAdapter: FavouriteRecyclerAdapter
    lateinit var favProgressLayout: RelativeLayout
    lateinit var favProgressBar: ProgressBar
    lateinit var favRecyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var empty:RelativeLayout
    var dbFoodList = arrayListOf<RestaurantEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)
        favProgressBar = view.findViewById(R.id.favProgressBar)
        favProgressLayout = view.findViewById(R.id.favProgressLayout)
        favRecyclerView = view.findViewById(R.id.favRecyclerView)
        favProgressLayout.visibility = View.VISIBLE
        empty=view.findViewById(R.id.empty)
        empty.visibility=View.GONE
        layoutManager = LinearLayoutManager(activity)
        dbFoodList=RetrieveFavourites(activity as Context).execute().get() as ArrayList<RestaurantEntity>
        if(dbFoodList.isEmpty()){
            favProgressLayout.visibility=View.GONE
            empty.visibility=View.VISIBLE

        }else {
            if (activity != null) {
                favProgressLayout.visibility = View.GONE
                favouriteRecyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbFoodList)
                favRecyclerView.adapter = favouriteRecyclerAdapter
                favRecyclerView.layoutManager = layoutManager
                favRecyclerView.addItemDecoration(
                    DividerItemDecoration(
                        favRecyclerView.context,
                        (layoutManager as LinearLayoutManager).orientation
                    )
                )

            }

        } 
        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
            return db.restaurantDao().getAllRestaurant()
        }

    }

}