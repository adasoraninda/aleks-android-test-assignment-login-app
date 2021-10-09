package com.adasoraninda.loginapplication.global

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adasoraninda.loginapplication.usecase.ObserveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val observeUserUseCase: ObserveUserUseCase
) : ViewModel() {

    private val _logout = MutableSharedFlow<Unit>()
    val logout: Flow<Unit> get() = _logout

    init {
        viewModelScope.launch {
            observeUserUseCase().onEach { user ->
                if (user == null) {
                    _logout.emit(Unit)
                }
            }.launchIn(this)
        }
    }

}