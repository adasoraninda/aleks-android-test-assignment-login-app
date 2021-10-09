package com.adasoraninda.loginapplication.usecase

import com.adasoraninda.loginapplication.model.db.dao.UserDao
import com.adasoraninda.loginapplication.model.db.dto.UserDto
import timber.log.Timber
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val userDao: UserDao
) {

    suspend operator fun invoke(userDto: UserDto) {
        Timber.d("invoke: ${userDto.email}")
        userDao.insert(userDto)
    }

}