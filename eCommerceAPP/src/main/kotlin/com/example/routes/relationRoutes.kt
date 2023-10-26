package com.example.routes

import com.example.database.Connection
import com.example.database.RelationImpl
import com.example.database.UserImpl
import com.example.model.AuthRequest
import com.example.model.Cart
import com.example.model.Order
import com.example.model.OrderState
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.relationRoutes() {

    val relationDao = RelationImpl()
    authenticate {
        route("/relations") {
            // Order route
            route("/order/{userID?}"){
                // Get all orders of user
                get{
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank()) return@get call.respondText("[ERROR] No valid ID has been entered.",
                        status = HttpStatusCode.BadRequest)

                    val orderList = relationDao.getOrders(userID.toInt())

                    return@get call.respond(HttpStatusCode.OK, orderList)
                }
                // Post new order
                post{
                    val newOrder = call.receiveNullable<Order>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                    val addOrder = relationDao.addOrder(newOrder)

                    if (addOrder){
                        return@post call.respondText("[SUCCESS] Order created", status = HttpStatusCode.Created)
                    } else {
                        return@post call.respondText("[ERROR] Order couldn't be created", status = HttpStatusCode.BadRequest)
                    }
                }
                // Put order state
                put("/{orderID?}") {
                    val orderID = call.parameters["orderID"]
                    if (orderID.isNullOrBlank()) return@put call.respondText("[ERROR] No valid ID has been entered.",
                        status = HttpStatusCode.BadRequest)

                    val newState = call.receiveNullable<OrderState>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@put
                    }
                    val putOrder = relationDao.putOrder(orderID.toInt(), newState)
                    if (putOrder){
                        return@put call.respondText("[SUCCESS] Order updated to $newState", status = HttpStatusCode.OK)
                    } else {
                        return@put call.respondText("[ERROR] Order couldn't be updated", status = HttpStatusCode.BadRequest)
                    }
                }
            }


            route("/cart/{userID?}"){
                // Get current cart of user
                get{
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank())
                        return@get call.respondText(
                            "[ERROR] No valid ID has been entered.",
                            status = HttpStatusCode.BadRequest
                        )
                    val currentCart = relationDao.getCart(userID.toInt())

                    if (currentCart != null){

                        call.respond(HttpStatusCode.OK, currentCart)
                        
                    } else {
                        call.respondText("[ERROR] This user has no cart..", status = HttpStatusCode.NotFound)
                    }
                }

                post {
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank())
                        return@post call.respondText(
                            "[ERROR] No valid ID has been entered.",
                            status = HttpStatusCode.BadRequest
                        )
                    val newCart = call.receiveNullable<Cart>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                    val addCart = relationDao.createCart(newCart)

                    if (addCart){
                        return@post call.respondText("[SUCCESS] Cart created", status = HttpStatusCode.Created)
                    } else {
                        return@post call.respondText("[ERROR] Cart couldn't be created", status = HttpStatusCode.BadRequest)
                    }
                }
                // Update product list of cart
                put {
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank())
                        return@put call.respondText(
                            "[ERROR] No valid ID has been entered.",
                            status = HttpStatusCode.BadRequest
                        )
                    val newProductList = call.receiveNullable<List<String>>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@put
                    }
                    val updateCart = relationDao.updateCart(userID.toInt(), newProductList)

                    if (updateCart){
                        return@put call.respondText("[SUCCESS] Cart updated", status = HttpStatusCode.OK)
                    } else {
                        return@put call.respondText("[ERROR] Cart couldn't be updated", status = HttpStatusCode.BadRequest)
                    }
                }

                delete {
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank())
                        return@delete call.respondText(
                            "[ERROR] No valid ID has been entered.",
                            status = HttpStatusCode.BadRequest
                        )
                    val cartID = relationDao.getCart(userID.toInt())!!.idCart
                    val deleteCart = relationDao.deleteCart(cartID)

                    if (deleteCart){
                        return@delete call.respondText("[SUCCESS] Cart deleted", status = HttpStatusCode.OK)
                    } else {
                        return@delete call.respondText("[ERROR] Cart couldn't be deleted", status = HttpStatusCode.BadRequest)
                    }
                }
            }

        }
    }




}