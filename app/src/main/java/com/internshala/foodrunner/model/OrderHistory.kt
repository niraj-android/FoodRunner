package com.internshala.foodrunner.model

import org.json.JSONArray

data class OrderHistory (
    val orderId:String,
    val restaurantName:String,
    val orderDate:String,
    val foodItem: JSONArray
)
