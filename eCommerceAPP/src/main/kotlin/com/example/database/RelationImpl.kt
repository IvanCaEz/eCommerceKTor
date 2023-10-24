package com.example.database

import com.example.model.Cart
import com.example.model.Order
import com.example.model.OrderState
import com.example.model.UserInfo
import java.sql.SQLException

class RelationImpl: RelationDao {

    private val connection = Connection.dbConnection()!!

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
        val sentenceSelect = "SELECT * FROM carts WHERE userID = $userID"
        try {
            var currentCart = Cart(0,userID, emptyList())
            val statement = connection.createStatement()
            val result = statement.executeQuery(sentenceSelect)
            while (result.next()) {
                val idCart = result.getInt(1)
                val idUser = result.getInt(2)
                val productArray = result.getArray(3)

                val productList = productArray.array.let { it as Array<String> }.toList()
                //Fill up with the information of a user searched by ID
                currentCart = Cart(idCart,idUser,productList)
            }
            //We close the sentence and connection to DB
            result.close()
            statement.close()
            return currentCart

        }catch (e: SQLException){
            println("[ERROR] Getting cart from user with ID: $userID | Error Code: ${e.errorCode}: ${e.message}")
            return null
        }
    }

    override fun addToCart(userID: Int, productID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun createCart(cart: Cart): Boolean {
        val sentenceInsert = "INSERT INTO carts VALUES" +
                "(DEFAULT, ?, ?)"

        try {
            val preparedInsert = connection.prepareStatement(sentenceInsert)
            preparedInsert.setInt(1, cart.idUser)
            // We need to convert the list of products to a varchar array to insert it into the DB
            preparedInsert.setArray(2, connection.createArrayOf("VARCHAR", cart.productList.toTypedArray()))

            //Execute the insert
            preparedInsert.executeUpdate()
            //We close the sentence and connection to DB
            preparedInsert.close()

            return true
        }catch (e: SQLException) {
            println("[ERROR] Failed inserting the cart | Error Code: ${e.errorCode}: ${e.message}")
            return false
        }
    }

    override fun removeFromCart(productID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteCart(cartID: Int): Boolean {
        TODO("Not yet implemented")
    }

}