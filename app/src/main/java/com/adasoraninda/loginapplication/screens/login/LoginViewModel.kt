package com.adasoraninda.loginapplication.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adasoraninda.loginapplication.usecase.GetForgottenPasswordUseCase
import com.adasoraninda.loginapplication.usecase.LoginUserUseCase
import com.adasoraninda.loginapplication.usecase.RegisterUserUseCase
import com.adasoraninda.loginapplication.utils.isValidEmail
import com.adasoraninda.loginapplication.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val getForgottenPasswordUseCase: GetForgottenPasswordUseCase
) : ViewModel() {

    private val _bottomSheetShow = MutableSharedFlow<Unit>()
    val bottomSheetShow: Flow<Unit> get() = _bottomSheetShow

    private val _forgotPasswordGetSuccess = MutableSharedFlow<String>()
    val forgotPasswordGetSuccess: Flow<String> get() = _forgotPasswordGetSuccess

    private val _registerSuccess = MutableSharedFlow<Unit>()
    val registerSuccess: Flow<Unit> get() = _registerSuccess

    private val _error = MutableSharedFlow<LoginErrorType>()
    val error: Flow<LoginErrorType> get() = _error

    private val _navigateToApp = MutableSharedFlow<Unit>()
    val navigateToApp: Flow<Unit> get() = _navigateToApp

    private val _state = MutableStateFlow(LoginState())
    val state: Flow<LoginState> get() = _state

    init {
        Timber.d("init")
    }

    fun loginClicked(email: String, password: String) {
        Timber.d("login clicked: $email, $password")
        if (validateInput(email, password)) {
            viewModelScope.launch {
                when (loginUserUseCase(email, password)) {
                    LoginUserUseCase.Result.Failure -> {
                        _error.emit(LoginErrorType.LOGIN)
                    }
                    LoginUserUseCase.Result.Success -> {
                        _navigateToApp.emit(Unit)
                    }
                }
            }
        }
    }

    fun registerClicked(email: String, password: String) {
        Timber.d("register clicked: $email, $password")
        if (validateInput(email, password)) {
            viewModelScope.launch {
                when (registerUserUseCase(email, password)) {
                    RegisterUserUseCase.Result.Failure -> {
                        // output same as login error. Fix later
                        _error.emit(LoginErrorType.SIGNUP)
                    }
                    RegisterUserUseCase.Result.Success -> {
                        loginUserUseCase(email, password)
                        _registerSuccess.emit(Unit)
                    }
                }
            }
        }
    }

    fun forgotPasswordSubmitClicked(email: String) {
        viewModelScope.launch {
            when (val result = getForgottenPasswordUseCase(email)) {
                GetForgottenPasswordUseCase.Result.Failure -> {
                    _error.emit(LoginErrorType.FORGOT_PASSWORD)
                }
                is GetForgottenPasswordUseCase.Result.Success -> {
                    _forgotPasswordGetSuccess.emit(result.password)
                }
            }
        }
    }

    fun forgotPasswordClicked() {
        viewModelScope.launch {
            _bottomSheetShow.emit(Unit)
        }
    }


    private fun validateInput(email: String, password: String): Boolean {
        val isEmailValid = email.isValidEmail()
        val isPasswordValid = password.isValidPassword()

        _state.value =
            _state.value.copy(isEmailValid = isEmailValid, isPasswordValid = isPasswordValid)

        return isEmailValid && isPasswordValid
    }

    fun onRegistrationSnackbarDismissed() {
        viewModelScope.launch {
            _navigateToApp.emit(Unit)
        }
    }

}

enum class LoginErrorType {
    LOGIN, SIGNUP, FORGOT_PASSWORD
}