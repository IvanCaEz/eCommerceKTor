package com.example.interfaces


import com.example.model.User

interface UserDaoInterface {

    fun getAllUsers(): List<User>?
    fun getUsertById(id: Int): User?
    fun addUser(user: User): Boolean
    fun deleteUser(id: Int): Boolean
    fun updateUser(user: User) : Boolean
}