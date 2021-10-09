package com.adasoraninda.loginapplication.screens.loggedin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adasoraninda.loginapplication.usecase.LogOutUseCase
import com.adasoraninda.loginapplication.usecase.ObserveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel @Inject constructor(
    private val loggedOutUseCase: LogOutUseCase,
    private val observeLoggedInUserUseCase: ObserveUserUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: Flow<String> get() = _email

    init {
        viewModelScope.launch {
            observeLoggedInUserUseCase().onEach { user ->
                Timber.d(user.toString())
                user?.let {
                    _email.emit(user.email)
                }
            }.launchIn(this)
        }
    }

    fun logOutClicked() {
        viewModelScope.launch {
            Timber.d("logout clicked invoked")
            loggedOutUseCase()
        }
    }

}