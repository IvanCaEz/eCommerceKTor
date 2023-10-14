package com.example.database


import com.example.model.User

interface UserDao {

    fun getAllUsers(): List<User>?
    fun getUserById(id: Int): User?
    fun getUserByEmail(email: String): User?
    fun addUser(user: User): Boolean
    fun deleteUser(id: Int): Boolean
    fun updateUser(user: User, id: Int) : Boolean
}