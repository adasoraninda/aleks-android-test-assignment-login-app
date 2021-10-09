package com.adasoraninda.loginapplication.usecase

import com.adasoraninda.loginapplication.utils.DATASTORE_LOGGED_IN_EMAIL_KEY
import com.adasoraninda.loginapplication.utils.DatastoreManager
import timber.log.Timber
import javax.inject.Inject

open class AddLoggedInEmailToDatastoreUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager,
) {

    open suspend operator fun invoke(email: String) {
        Timber.d("invoke: $email")
        datastoreManager.addToDatastore(DATASTORE_LOGGED_IN_EMAIL_KEY, email)
    }

}