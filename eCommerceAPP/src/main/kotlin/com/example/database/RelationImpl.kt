package com.example.database

import com.example.model.Cart
import com.example.model.Order
import com.example.model.OrderStates

class RelationImpl: RelationDao {
    override fun getOrders(userID: Int): List<Order>? {
        TODO("Not yet implemented")
    }

    override fun addOrder(userID: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun putOrder(orderID: Int, state: OrderStates): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCart(userID: Int): Cart? {
        TODO("Not yet implemented")
    }

    override fun addToCart(userID: Int, productID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun createCart(userID: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeFromCart(productID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteCart(cartID: Int): Boolean {
        TODO("Not yet implemented")
    }
}