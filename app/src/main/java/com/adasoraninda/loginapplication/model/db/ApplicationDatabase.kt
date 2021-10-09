package com.adasoraninda.loginapplication.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adasoraninda.loginapplication.model.db.dao.UserDao
import com.adasoraninda.loginapplication.model.db.dto.UserDto

@Database(entities = [UserDto::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}