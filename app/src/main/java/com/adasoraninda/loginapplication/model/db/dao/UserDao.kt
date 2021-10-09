package com.adasoraninda.loginapplication.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.adasoraninda.loginapplication.model.db.dto.UserDto

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun findByEmail(email: String): UserDto

    @Query("SELECT EXISTS(SELECT * FROM user WHERE email = :email)")
    suspend fun isUserExists(email: String): Boolean

    @Insert
    suspend fun insert(user: UserDto)

}