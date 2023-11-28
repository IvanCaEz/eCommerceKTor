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
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.relationRoutes(userDao: UserImpl, relationDao: RelationImpl) {


    authenticate {
        route("/relations") {
            // Order route
            route("/order/{userID?}"){
                // Get all orders of user
                get{
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank()) return@get call.respondText("[ERROR] No valid ID has been entered.",
                        status = HttpStatusCode.BadRequest)
                    call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let { mail ->
                        userDao.getUserByEmail(mail)?.let { userInfo ->

                            if (userInfo.userID != userID.toInt()){
                                return@get call.respondText("[ERROR] You can't access to this user's orders.",
                                    status = HttpStatusCode.Unauthorized)
                            }

                            val orderList = relationDao.getOrders(userID.toInt())

                            return@get call.respond(HttpStatusCode.OK, orderList)

                        }
                    }


                }
                // Post new order
                post{
                val userID = call.parameters["userID"]
                if (userID.isNullOrBlank()) return@post call.respondText("[ERROR] No valid ID has been entered.",
                    status = HttpStatusCode.BadRequest)

                val newOrder = call.receiveNullable<Order>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }

                    call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let { mail ->
                        userDao.getUserByEmail(mail)?.let { userInfo ->

                            if (userInfo.userID != userID.toInt()) {
                                return@post call.respondText(
                                    "[ERROR] You can't create orders for another user.",
                                    status = HttpStatusCode.Unauthorized
                                )
                            }
                            val addOrder = relationDao.addOrder(newOrder)

                            if (addOrder){
                                return@post call.respond(HttpStatusCode.Created, true)
                            } else {
                                return@post call.respond(HttpStatusCode.BadRequest, false)
                            }
                        }
                    }
                }
                // Put order state
                put("/{orderID?}") {
                    val orderID = call.parameters["orderID"]
                    if (orderID.isNullOrBlank()) return@put call.respondText("[ERROR] No valid order ID has been entered.",
                        status = HttpStatusCode.BadRequest)
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank()) return@put call.respondText("[ERROR] No valid user ID has been entered.",
                        status = HttpStatusCode.BadRequest)

                    val newState = call.receiveNullable<OrderState>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@put
                    }

                    call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let { mail ->
                        userDao.getUserByEmail(mail)?.let { userInfo ->

                            if (userInfo.userID != userID.toInt()) {
                                return@put call.respondText(
                                    "[ERROR] You can't update this user's orders.",
                                    status = HttpStatusCode.Unauthorized
                                )
                            }
                            val putOrder = relationDao.putOrder(orderID.toInt(), newState)

                            if (putOrder){
                                return@put call.respond(HttpStatusCode.Created, true)
                            } else {
                                return@put call.respond(HttpStatusCode.BadRequest, false)
                            }
                        }
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
                    call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let { mail ->
                        userDao.getUserByEmail(mail)?.let { userInfo ->

                            if (userInfo.userID != userID.toInt()) {
                                return@get call.respondText(
                                    "[ERROR] You can't access to this user's cart.",
                                    status = HttpStatusCode.Unauthorized
                                )
                            }
                            val currentCart = relationDao.getCart(userID.toInt())

                            if (currentCart != null){
                                call.respond(HttpStatusCode.OK, currentCart)
                            } else {
                                call.respondText("[ERROR] This user has no cart..", status = HttpStatusCode.NotFound)
                            }
                        }
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
                    call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let { mail ->
                        userDao.getUserByEmail(mail)?.let { userInfo ->

                            if (userInfo.userID != userID.toInt()) {
                                return@post call.respondText(
                                    "[ERROR] You can't access to this user's cart.",
                                    status = HttpStatusCode.Unauthorized
                                )
                            }

                            val addCart = relationDao.createCart(newCart)

                            if (addCart){
                                return@post call.respond(HttpStatusCode.Created, true)
                            } else {
                                return@post call.respond(HttpStatusCode.BadRequest, false)
                            }
                        }
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
                    call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let { mail ->
                        userDao.getUserByEmail(mail)?.let { userInfo ->

                            if (userInfo.userID != userID.toInt()) {
                                return@put call.respondText(
                                    "[ERROR] You can't access to this user's cart.",
                                    status = HttpStatusCode.Unauthorized
                                )
                            }
                            val updateCart = relationDao.updateCart(userID.toInt(), newProductList)

                            if (updateCart){
                                return@put call.respond(HttpStatusCode.OK, true)
                            } else {
                                return@put call.respond(HttpStatusCode.BadRequest, false)
                            }
                        }
                    }
                    }
                // Delete cart
                delete {
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank())
                        return@delete call.respondText(
                            "[ERROR] No valid ID has been entered.",
                            status = HttpStatusCode.BadRequest
                        )

                    call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let { mail ->
                        userDao.getUserByEmail(mail)?.let { userInfo ->

                            if (userInfo.userID != userID.toInt()) {
                                return@delete call.respondText(
                                    "[ERROR] You can't delete other user's carts.",
                                    status = HttpStatusCode.Unauthorized
                                )
                            }
                            val cartID = relationDao.getCart(userID.toInt())!!.idCart
                            val deleteCart = relationDao.deleteCart(cartID)

                            if (deleteCart){
                                return@delete call.respond(HttpStatusCode.OK, true)
                            } else {
                                return@delete call.respond(HttpStatusCode.BadRequest, false)
                            }
                        }
                    }
                }
            }
        }
    }
}