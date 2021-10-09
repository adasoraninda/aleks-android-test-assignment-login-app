package com.adasoraninda.loginapplication.usecase

import com.adasoraninda.loginapplication.utils.DATASTORE_LOGGED_IN_EMAIL_KEY
import com.adasoraninda.loginapplication.utils.DatastoreManager
import timber.log.Timber
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {

    suspend operator fun invoke() {
        Timber.d("invoke")
        datastoreManager.removeFromDataStore(DATASTORE_LOGGED_IN_EMAIL_KEY)
    }

}