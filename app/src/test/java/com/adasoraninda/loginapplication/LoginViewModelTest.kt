package com.adasoraninda.loginapplication

import app.cash.turbine.test
import com.adasoraninda.loginapplication.screens.login.LoginErrorType
import com.adasoraninda.loginapplication.screens.login.LoginState
import com.adasoraninda.loginapplication.screens.login.LoginViewModel
import com.adasoraninda.loginapplication.usecase.GetForgottenPasswordUseCase
import com.adasoraninda.loginapplication.usecase.LoginUserUseCase
import com.adasoraninda.loginapplication.usecase.RegisterUserUseCase
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var loginUserUseCase: LoginUserUseCase
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var getForgottenPasswordUseCase: GetForgottenPasswordUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun setupViewModel(
        loginUserUseCase: LoginUserUseCase = mock(LoginUserUseCase::class.java),
        registerUserUseCase: RegisterUserUseCase = mock(RegisterUserUseCase::class.java),
        getForgottenPasswordUseCase: GetForgottenPasswordUseCase = mock(GetForgottenPasswordUseCase::class.java)
    ): LoginViewModel {
        this.loginUserUseCase = loginUserUseCase
        this.registerUserUseCase = registerUserUseCase
        this.getForgottenPasswordUseCase = getForgottenPasswordUseCase

        return LoginViewModel(
            loginUserUseCase = this.loginUserUseCase,
            registerUserUseCase = this.registerUserUseCase,
            getForgottenPasswordUseCase = this.getForgottenPasswordUseCase
        )
    }

    @Test
    fun loginClicked_validateInputs() {
        val viewModel = setupViewModel()
        val invalidUsername = ""
        val invalidPassword = ""

        runBlockingTest {
            viewModel.state.test {
                assertEquals(LoginState(), awaitItem())
                viewModel.loginClicked(invalidUsername, invalidPassword)
                verify(loginUserUseCase, never()).invoke(invalidUsername, invalidPassword)
                assertEquals(LoginState(false, false), awaitItem())
            }
        }
    }

    @Test
    fun loginClicked_inputGood_loginError() {
        val viewModel = setupViewModel(
            loginUserUseCase = mock(LoginUserUseCase::class.java) {
                LoginUserUseCase.Result.Failure
            }
        )

        val username = "ada@ada.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.error.test {
                viewModel.loginClicked(username, password)
                verify(loginUserUseCase, atLeastOnce()).invoke(username, password)
                assertEquals(LoginErrorType.LOGIN, awaitItem())
            }
        }
    }

    @Test
    fun loginClicked_inputGood_loginSuccess() {
        val viewModel = setupViewModel(
            loginUserUseCase = mock(LoginUserUseCase::class.java) {
                LoginUserUseCase.Result.Success
            }
        )

        val invalidUsername = ""
        val invalidPassword = ""
        val username = "ada@ada.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.state.test {
                assertEquals(LoginState(), awaitItem())
                viewModel.loginClicked(invalidUsername, invalidPassword)
                assertEquals(LoginState(isEmailValid = false, isPasswordValid = false), awaitItem())
                viewModel.loginClicked(username, password)
                assertEquals(LoginState(), awaitItem())
            }

            viewModel.error.test {
                viewModel.loginClicked(username, password)
                expectNoEvents()
            }

            viewModel.navigateToApp.test {
                viewModel.loginClicked(username, password)
                assertEquals(Unit, awaitItem())
            }

            verify(loginUserUseCase, atLeast(3)).invoke(username, password)
        }
    }

    @Test
    fun signupClicked_inputsGood_registerError() {
        val viewModel = setupViewModel(
            registerUserUseCase = mock(RegisterUserUseCase::class.java) {
                RegisterUserUseCase.Result.Failure
            }
        )

        val username = "ada@ada.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.error.test {
                viewModel.registerClicked(username, password)
                verify(registerUserUseCase, atLeastOnce()).invoke(username, password)
                assertEquals(LoginErrorType.SIGNUP, awaitItem())
            }
        }
    }

    @Test
    fun signupClicked_inputsGood_registerSuccess() {
        val viewModel = setupViewModel(
            loginUserUseCase = mock(LoginUserUseCase::class.java) {
                LoginUserUseCase.Result.Success
            },
            registerUserUseCase = mock(RegisterUserUseCase::class.java) {
                RegisterUserUseCase.Result.Success
            }
        )

        val invalidUsername = ""
        val invalidPassword = ""
        val username = "ada@ada.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.state.test {
                assertEquals(LoginState(), awaitItem())
                viewModel.registerClicked(invalidUsername, invalidPassword)
                assertEquals(LoginState(isEmailValid = false, isPasswordValid = false), awaitItem())
                viewModel.registerClicked(username, password)
                assertEquals(LoginState(), awaitItem())
            }

            viewModel.error.test {
                viewModel.registerClicked(username, password)
                expectNoEvents()
            }

            viewModel.registerSuccess.test {
                viewModel.registerClicked(username, password)
                assertEquals(Unit, awaitItem())
            }

            verify(registerUserUseCase, atLeast(3)).invoke(username, password)
            verify(loginUserUseCase, atLeast(1)).invoke(username, password)
        }
    }

    @Test
    fun forgotPasswordClicked() {
        val viewModel = setupViewModel()

        runBlockingTest {
            viewModel.bottomSheetShow.test {
                viewModel.forgotPasswordClicked()
                assertEquals(Unit, awaitItem())
            }
        }
    }

    @Test
    fun forgotPasswordSubmitClicked_success() {
        val email = "ada@ada.com"
        val password = "12345678"

        val viewModel = setupViewModel(
            getForgottenPasswordUseCase = mock(GetForgottenPasswordUseCase::class.java) {
                GetForgottenPasswordUseCase.Result.Success(password)
            }
        )

        runBlockingTest {
            viewModel.forgotPasswordGetSuccess.test {
                viewModel.forgotPasswordSubmitClicked(email)
                verify(getForgottenPasswordUseCase, atLeastOnce()).invoke(email)
                assertEquals(password, awaitItem())
            }
        }
    }

    @Test
    fun forgotPasswordSubmitClicked_failure() {
        val email = "ada@ada.com"

        val viewModel = setupViewModel(
            getForgottenPasswordUseCase = mock(GetForgottenPasswordUseCase::class.java) {
                GetForgottenPasswordUseCase.Result.Failure
            }
        )

        runBlockingTest {
            viewModel.error.test {
                viewModel.forgotPasswordSubmitClicked(email)
                verify(getForgottenPasswordUseCase, atLeastOnce()).invoke(email)
                assertEquals(LoginErrorType.FORGOT_PASSWORD, awaitItem())
            }
        }
    }

    @Test
    fun onRegistrationSnackbarDismissed() {
        val viewModel = setupViewModel()
        runBlockingTest {
            viewModel.navigateToApp.test {
                viewModel.onRegistrationSnackbarDismissed()
                assertEquals(Unit, awaitItem())
            }
        }
    }

}