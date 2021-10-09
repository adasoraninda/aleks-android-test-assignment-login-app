package com.adasoraninda.loginapplication

import com.adasoraninda.loginapplication.model.db.dto.UserDto
import com.adasoraninda.loginapplication.usecase.AddLoggedInEmailToDatastoreUseCase
import com.adasoraninda.loginapplication.usecase.GetUserByEmailUseCase
import com.adasoraninda.loginapplication.usecase.LoginUserUseCase
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class LoginUserUseCaseTest {

    private lateinit var getUserByEmailUseCase: GetUserByEmailUseCase
    private lateinit var addLoggedInEmailToDatastoreUseCase: AddLoggedInEmailToDatastoreUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun setupUseCase(
        getUserByEmailUseCase: GetUserByEmailUseCase = mock(GetUserByEmailUseCase::class.java),
        addLoggedInEmailToDatastoreUseCase: AddLoggedInEmailToDatastoreUseCase = mock(
            AddLoggedInEmailToDatastoreUseCase::class.java
        )
    ): LoginUserUseCase {
        this.getUserByEmailUseCase = getUserByEmailUseCase
        this.addLoggedInEmailToDatastoreUseCase = addLoggedInEmailToDatastoreUseCase

        return LoginUserUseCase(
            this.getUserByEmailUseCase,
            this.addLoggedInEmailToDatastoreUseCase
        )
    }

    @Test
    fun userNotFound() {
        val email = "ada@ada.com"
        val password = "12345678"

        val usecase = setupUseCase(
            getUserByEmailUseCase = mock(GetUserByEmailUseCase::class.java) {
                throw Exception("User not found")
            }
        )

        runBlockingTest {
            val result = usecase(email, password)
            verify(getUserByEmailUseCase, atLeastOnce()).invoke(email)
            assertEquals(LoginUserUseCase.Result.Failure, result)
        }
    }

    @Test
    fun passwordDoNotMatch() {
        val email = "ada@ada.com"
        val dbPassword = "12345678"
        val methodInputPassword = "password"

        val usecase = setupUseCase(getUserByEmailUseCase = mock(GetUserByEmailUseCase::class.java) {
            UserDto(0, email, dbPassword)
        })

        runBlockingTest {
            val result = usecase(email, methodInputPassword)
            verify(getUserByEmailUseCase, atLeastOnce()).invoke(email)
            assertEquals(LoginUserUseCase.Result.Failure, result)
        }
    }

    @Test
    fun success() {
        val email = "ada@ada.com"
        val password = "12345678"

        val usecase = setupUseCase(getUserByEmailUseCase = mock(GetUserByEmailUseCase::class.java) {
            UserDto(0, email, password)
        })

        runBlockingTest {
            val result = usecase(email, password)
            verify(getUserByEmailUseCase, atLeastOnce()).invoke(email)
            verify(addLoggedInEmailToDatastoreUseCase, atLeastOnce()).invoke(email)
            assertEquals(LoginUserUseCase.Result.Success, result)
        }
    }

}