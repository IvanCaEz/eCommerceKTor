package com.example.dao

import com.example.interfaces.UserDaoInterface
import com.example.model.User
import java.sql.SQLException

class UserDao : UserDaoInterface {

    private val connection = ADao.connection!!

    override fun getAllUsers(): List<User>? {
        val sentenceSelect = "SELECT * FROM user_info"
        val users = mutableListOf<User>()

        try {
            val statement = connection.createStatement()
            val result = statement.executeQuery(sentenceSelect)
            while (result.next()) {
                val userID = result.getInt(1)
                val userImage = result.getString(2)
                val userEmail = result.getString(3)
                val userPass = result.getString(4)

                //We build a User object and putting data into the mutableList
                users.add(User(userID,userImage,userEmail,userPass))
            }
            //We close the sentence and connection to DB
            result.close()
            statement.close()

        } catch (e: SQLException) {
            println("[ERROR] Getting all users failing | Error Code:${e.errorCode}: ${e.message}")
        }
            return users.ifEmpty { null }
    }


    override fun getUsertById(id: Int): User? {
        val sentenceSelect = "SELECT * FROM user_info WHERE userID = $id"
        var userByID = User(99,"","","")
        try {
            val statement = connection.createStatement()
            val result = statement.executeQuery(sentenceSelect)
            while (result.next()) {
                val userID = result.getInt(1)
                val userImage = result.getString(2)
                val userEmail = result.getString(3)
                val userPass = result.getString(4)

                //Fill up with the information of a user searched by ID
                userByID = User(userID,userImage,userEmail,userPass)
            }
            //We close the sentence and connection to DB
            result.close()
            statement.close()
            return userByID

        }catch (e: SQLException){
            println("[ERROR] Getting data from user with ID: $id | Error Code:${e.errorCode}: ${e.message}")
            return null
        }
    }

    override fun addUser(user: User): Boolean {
        val sentenceInsert = "INSERT INTO user_info VALUES" +
                             "(DEFAULT, ?, ?, ?)"

        try {
            val preparedInsert = connection.prepareStatement(sentenceInsert)
            user.userID?.let { preparedInsert.setInt(1, it) }
            preparedInsert.setString(2, user.userImage)
            preparedInsert.setString(3, user.userEmail)
            preparedInsert.setString(3, user.userPass)

            //We execute the insert
            preparedInsert.executeQuery()
            //We close the sentence and connection to DB
            preparedInsert.close()

            return true
        }catch (e: SQLException) {
            println("[ERROR] Failed inserting user | Error Code:${e.errorCode}: ${e.message}")
            return false
        }
    }

    override fun deleteUser(id: Int): Boolean {
        val sentenceDelete = "DELETE FROM user_info WHERE userID = $id"

        try {
            val preparedDelete = connection.prepareStatement(sentenceDelete)
            //We execute the delete
            val rowsDeleted = preparedDelete.executeQuery()
            println("$rowsDeleted row(s) deleted")
            //We close the sentence and connection to DB
            preparedDelete.close()

            return true
        }catch (e: SQLException){
            println("[ERROR] Failed deletting user | Error Code:${e.errorCode}: ${e.message}")
            return false
        }
    }

    override fun updateUser(user: User, id: Int): Boolean {
        val sentenceUpdate = "UPDATE user_info SET " +
                "userImage = ?, userEmail = ?,  userPass = ?" +
                "WHERE userID = $id"

        try {
            val preparedUpdate = connection.prepareStatement(sentenceUpdate)

            preparedUpdate.setString(1, user.userImage)
            preparedUpdate.setString(2, user.userEmail)
            preparedUpdate.setString(3, user.userPass)
            //WHERE
            //user.userID?.let { preparedUpdate.setInt(4, it) }

            //We execute the insert
            preparedUpdate.executeUpdate()
            //We close the sentence and connection to DB
            preparedUpdate.close()
            return true
        } catch (e: SQLException) {
            println("[ERROR] Failed updating User | Error Code:${e.errorCode}: ${e.message}")
            return false
        }
    }



}
