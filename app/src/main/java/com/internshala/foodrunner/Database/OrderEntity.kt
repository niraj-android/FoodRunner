package com.internshala.foodrunner.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="orders")
data class OrderEntity (

    @PrimaryKey
    val foodId: String,
    @ColumnInfo(name = "name") val foodName: String,
    @ColumnInfo(name = "cost_for_one") val Price: String,
    @ColumnInfo(name = "restaurant_id") val restaurantId: String
)
