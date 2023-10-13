package com.example.routes


import com.example.dao.*
import com.example.model.User
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.userRoutes() {

    val dao = UserDao()

    route("/allusers") {

        get {
            val allUsers = dao.getAllUsers()

            allUsers?.let {
                call.respond(it)
            } ?: call.respondText("[ERROR] There are no users in the database.", status = HttpStatusCode.NotFound)
        }

        post {
            val newUser = call.receive<User>()
            val addedNewUser = dao.addUser(newUser)

            println(addedNewUser)
            if (addedNewUser) {
                return@post call.respondText("[SUCCESS] New user created successfully.", status = HttpStatusCode.Created)
            } else {
                return@post call.respondText("[ERROR] The new user could not be added.", status = HttpStatusCode.BadRequest)
            }
        }

        get("/{id?}") {
            if (call.parameters["id"].isNullOrBlank())
                return@get call.respondText("[ERROR] No valid ID has been entered.", status = HttpStatusCode.BadRequest)

            val id = call.parameters["id"]!!.toInt()
            val userByID = dao.getUsertById(id)



            if (userByID != null) {
                return@get call.respond(userByID!!)
            } else {
                return@get call.respondText("[ERROR] No user with the ID ($id) exists.", status = HttpStatusCode.NotFound)
            }

        }

        delete("/{id?}") {
            if (call.parameters["id"].isNullOrBlank())
                return@delete call.respondText("[ERROR] No valid ID has been entered.", status = HttpStatusCode.BadRequest)

            val id = call.parameters["id"]!!.toInt()

            val deleteUser = dao.deleteUser(id)

            if (deleteUser) {
                return@delete call.respondText("[SUCCESS] User successfully deleted.", status = HttpStatusCode.Created)
            } else {
                return@delete call.respondText("[ERROR] The user could not be deleted.", status = HttpStatusCode.BadRequest)
            }
        }

        put("/{id?}") {
            if (call.parameters["id"].isNullOrBlank())
                return@put call.respondText("[ERROR] No valid ID has been entered.", status = HttpStatusCode.BadRequest)

            val id = call.parameters["id"]!!.toInt()
            val user = User(99,"","","")
            val data = call.receiveMultipart()

            data.forEachPart {part ->
                when(part){
                    is PartData.FormItem ->{
                        when(part.name){
                            "userID" -> user.userID = part.value.toInt()
                            "userEmail" -> user.userEmail = part.value
                            "userPass" -> user.userPass = part.value
                        }
                    }
                    is PartData.FileItem ->{
                        user.userImage = "uploads/" + part.originalFileName as String // A user.image le asignamos en formato string la ruta donde se guardará la imagen

                        val fileBytes = part.streamProvider().readBytes() //LEEMOS LA IMAGEN QUE HA PASADO POR EL POST
                        File(user.userImage).writeBytes(fileBytes)//GUARDA LA IMAGEN QUE HA PASADO POR EL POST A LA CARPETA "uploads"
                        //EN BASES DE DATOS SOLO GUARDAR URL del archivo
                    }
                    else -> {}
                }
            }

            val updateUser = dao.updateUser(user, id)

            if (updateUser) {
                return@put call.respondText("[SUCCESS] User successfully modified.", status = HttpStatusCode.Created)
            } else {
                return@put call.respondText("[ERROR] The user could not be modified.", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

