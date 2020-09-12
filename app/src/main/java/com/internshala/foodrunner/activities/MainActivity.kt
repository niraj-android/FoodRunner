package com.internshala.foodrunner.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.foodrunner.*
import com.internshala.foodrunner.fragments.*

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout:CoordinatorLayout
    lateinit var toolbar:Toolbar
    lateinit var frameLayout:FrameLayout
    lateinit var navigationView:NavigationView
    var previousMenuItem:MenuItem?=null
   lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setUpToolbar()
        openHome()

val view=navigationView.inflateHeaderView(R.layout.drawer_header)
        var txtUserName:TextView=view.findViewById(R.id.txtUserName)
        var userName="${sharedPreferences.getString("user_name","") }"
        txtUserName.text= userName
        var txtUserNumber:TextView=view.findViewById(R.id.txtUserNumber)
        var userNumber="+91-${sharedPreferences.getString("user_mobile_number","")}"
        txtUserNumber.text= userNumber




        val actionBarDrawerToggle=ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem!=null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it


            when(it.itemId){
                R.id.home ->{
                   openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.userProfile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, UserProfileFragment())
                        .commit()
                    supportActionBar?.title="User Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favRestaurant ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouriteRestaurantFragment())
                        .commit()
                    supportActionBar?.title="Favourite Restaurant"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FAQsFragment())
                        .commit()
                    supportActionBar?.title="FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.logout ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, LogoutFragment())
                        .commit()
                    supportActionBar?.title="Logout"
                    drawerLayout.closeDrawers()
                }


            }

            return@setNavigationItemSelectedListener true

        }

    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id= item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome(){
        val fragment= HomeFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        supportActionBar?.title="Restaurants"
        navigationView.setCheckedItem(R.id.home)

    }

    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            !is HomeFragment -> openHome()

            else -> super.onBackPressed()
        }
    }
}