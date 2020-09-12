package com.internshala.foodrunner.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert
    fun insertOrder(orderEntity: OrderEntity)
    @Delete
    fun deleteOrder(orderEntity: OrderEntity)
    @Query("SELECT * FROM orders WHERE foodId=:id")
    fun getOrderById(id:String):OrderEntity

    @Query("SELECT * FROM orders")
    fun getAllOrders():List<OrderEntity>
    @Query("DELETE FROM orders WHERE restaurant_id=:id" )
    fun deleteOrderById(id: String)

}