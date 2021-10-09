package com.adasoraninda.loginapplication.usecase

import timber.log.Timber
import javax.inject.Inject


open class LoginUserUseCase @Inject constructor(
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val addLoggedInEmailToDatastoreUseCase: AddLoggedInEmailToDatastoreUseCase
) {

    open suspend operator fun invoke(email: String, password: String): Result {
        Timber.d("invoke: $email")
        try {
            val userDto = getUserByEmailUseCase(email)

            if (userDto.password != password) {
                Timber.e("failed, passwords do not match")
                return Result.Failure
            }

            Timber.d("login successfully")
            addLoggedInEmailToDatastoreUseCase(email)
            return Result.Success
        } catch (e: Exception) {
            Timber.e("failed, exception: ${e.message}")
            return Result.Failure
        }
    }

    sealed class Result {
        object Success : Result()
        object Failure : Result()
    }

}