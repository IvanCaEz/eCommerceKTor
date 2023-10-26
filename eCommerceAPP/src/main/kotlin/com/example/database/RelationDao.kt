package com.example.database

import com.example.model.Cart
import com.example.model.Order
import com.example.model.OrderState

interface RelationDao {
    // Order
    fun getOrders(userID: Int): List<Order>
    fun addOrder(order: Order): Boolean
    fun putOrder(orderID: Int, state: OrderState): Boolean

    // Cart
    fun getCart(userID: Int): Cart?
    fun createCart(cart: Cart): Boolean
    fun updateCart(userID: Int, productList: List<String>): Boolean
    fun deleteCart(cartID: Int): Boolean
}