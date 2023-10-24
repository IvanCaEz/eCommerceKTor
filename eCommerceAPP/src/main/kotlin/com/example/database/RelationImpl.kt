package com.example.database

import com.example.model.Cart
import com.example.model.Order
import com.example.model.OrderState

class RelationImpl: RelationDao {
    override fun getOrders(userID: Int): List<Order> {
        TODO("Not yet implemented")
    }

    override fun addOrder(order: Order): Boolean {
        TODO("Not yet implemented")
    }

    override fun putOrder(orderID: Int, state: OrderState): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCart(userID: Int): Cart? {
        TODO("Not yet implemented")
    }

    override fun addToCart(userID: Int, productID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun createCart(cart: Cart): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeFromCart(productID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteCart(cartID: Int): Boolean {
        TODO("Not yet implemented")
    }

}