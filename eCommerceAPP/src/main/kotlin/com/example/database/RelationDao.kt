package com.example.database

import com.example.model.Cart
import com.example.model.Order
import com.example.model.OrderStates

interface RelationDao {
    // Order
    fun getOrders(userID: Int): List<Order>?
    fun addOrder(userID: Int): Boolean
    fun putOrder(orderID: Int, state: OrderStates): Boolean

    // Cart
    fun getCart(userID: Int): Cart?
    fun addToCart(userID: Int, productID: String): Boolean
    fun createCart(userID: Int): Boolean
    fun removeFromCart(productID: String): Boolean
    fun deleteCart(cartID: Int): Boolean
}