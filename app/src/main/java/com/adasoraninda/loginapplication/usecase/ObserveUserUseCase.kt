package com.adasoraninda.loginapplication.usecase

import com.adasoraninda.loginapplication.model.domain.User
import com.adasoraninda.loginapplication.utils.DATASTORE_LOGGED_IN_EMAIL_KEY
import com.adasoraninda.loginapplication.utils.DatastoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveUserUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {

    operator fun invoke(): Flow<User?> {
        return datastoreManager.observeKeyValue(DATASTORE_LOGGED_IN_EMAIL_KEY).map { email ->
            if (email != null) {
                User(email = email)
            } else {
                null
            }
        }
    }

}