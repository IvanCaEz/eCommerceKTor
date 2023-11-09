package com.example.database

import com.example.model.Cart
import com.example.model.Order
import com.example.model.OrderState
import java.sql.SQLException

class RelationImpl: RelationDao {

    private val connection = Connection.dbConnection()!!

    override fun getOrders(userID: Int): List<Order> {
        val sentenceSelect = "SELECT * FROM orders WHERE userID = $userID"
        val orderList = emptyList<Order>().toMutableList()
        try {
            val statement = connection.createStatement()
            val result = statement.executeQuery(sentenceSelect)
            while (result.next()) {
                val idOrder = result.getInt(1)
                val idUser = result.getInt(2)
                val productArray = result.getArray(3)
                val totalPrice = result.getDouble(4)
                val orderDate = result.getString(5)
                val state = result.getString(6)

                // Convert the java array to a kotlin list
                val productList = productArray.array.let { it as Array<String> }.toList()

                // Make the order object and add it to the list
                val order = Order(idOrder,idUser,productList,totalPrice,orderDate, OrderState.valueOf(state))

                // Add the order to the orderList
                orderList.add(order)

            }
            //We close the sentence and connection to DB
            result.close()
            statement.close()
            return orderList

        }catch (e: SQLException){
            println("[ERROR] Getting orderList from user with ID: $userID | Error Code: ${e.errorCode}: ${e.message}")
            return orderList
        }
    }

    override fun addOrder(order: Order): Boolean {
        val sentenceInsert = "INSERT INTO orders VALUES" +
                "(DEFAULT, ?, ?, ?, ?, CAST(? AS order_state))"

        try {
            val preparedInsert = connection.prepareStatement(sentenceInsert)
            preparedInsert.setInt(1, order.idUser)
            // We need to convert the list of the products id to a varchar array to insert it into the DB
            preparedInsert.setArray(2, connection.createArrayOf("VARCHAR", order.productList.toTypedArray()))
            preparedInsert.setDouble(3, order.totalPrice)
            preparedInsert.setString(4, order.orderDate)
            preparedInsert.setString(5, OrderState.ONGOING.toString())

            // Execute the insert
            preparedInsert.executeUpdate()
            // Close the sentence
            preparedInsert.close()

            return true
        }catch (e: SQLException) {
            println("[ERROR] Failed inserting the order | Error Code: ${e.errorCode}: ${e.message}")
            return false
        }
    }

    override fun putOrder(orderID: Int, state: OrderState): Boolean {
        val sentenceUpdate = "UPDATE orders SET " +
                "order_state = CAST(? AS order_state)" +
                " WHERE orderID = ?"

        return try {
            val preparedUpdate = connection.prepareStatement(sentenceUpdate)
            preparedUpdate.setString(1, state.toString())
            preparedUpdate.setInt(2, orderID)
            // Execute the update
            preparedUpdate.executeUpdate()
            // Close the sentence and connection to DB
            preparedUpdate.close()
            true
        } catch (e: SQLException) {
            println("[ERROR] Failed updating UserInfo | Error Code:${e.errorCode}: ${e.message}")
            false
        }
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
                // Make the cart object
                currentCart = Cart(idCart,idUser,productList)
            }
            // Close the sentence
            result.close()
            statement.close()
            return currentCart

        }catch (e: SQLException){
            println("[ERROR] Getting cart from user with ID: $userID | Error Code: ${e.errorCode}: ${e.message}")
            return null
        }
    }


    override fun createCart(cart: Cart): Boolean {
        val sentenceInsert = "INSERT INTO carts VALUES" +
                "(DEFAULT, ?, ?)"

        try {
            val preparedInsert = connection.prepareStatement(sentenceInsert)
            preparedInsert.setInt(1, cart.idUser)
            // We need to convert the list of products id to a varchar array to insert it into the DB
            preparedInsert.setArray(2, connection.createArrayOf("VARCHAR", cart.productList.toTypedArray()))

            // Execute the insert
            preparedInsert.executeUpdate()
            // Close the sentence
            preparedInsert.close()

            return true
        }catch (e: SQLException) {
            println("[ERROR] Failed inserting the cart | Error Code: ${e.errorCode}: ${e.message}")
            return false
        }
    }

    override fun updateCart(userID: Int, productList: List<String>): Boolean {
        val sentenceUpdate = "UPDATE carts SET " +
                "productList = ?" +
                " WHERE userID = ?"

        return try {
            val preparedUpdate = connection.prepareStatement(sentenceUpdate)
            preparedUpdate.setArray(1, connection.createArrayOf("VARCHAR", productList.toTypedArray()))
            preparedUpdate.setInt(2, userID)
            // Execute the update
            preparedUpdate.executeUpdate()
            // Close the sentence and connection to DB
            preparedUpdate.close()
            true
        } catch (e: SQLException) {
            println("[ERROR] Failed updating UserInfo | Error Code:${e.errorCode}: ${e.message}")
            false
        }
    }


    override fun deleteCart(cartID: Int): Boolean {
        val sentenceDelete = "DELETE FROM carts WHERE cartID = $cartID"

        return try {
            val preparedDelete = connection.prepareStatement(sentenceDelete)
            // Execute the delete
            preparedDelete.executeUpdate()
            // Close the sentence and connection to DB
            preparedDelete.close()

            true
        }catch (e: SQLException){
            println("[ERROR] Failed deletting cart | Error Code:${e.errorCode}: ${e.message}")
            false
        }
    }

}