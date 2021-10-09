package com.adasoraninda.loginapplication.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.adasoraninda.loginapplication.R
import com.adasoraninda.loginapplication.databinding.ForgotPasswordDialogBinding
import com.adasoraninda.loginapplication.databinding.FragmentLoginBinding
import com.adasoraninda.loginapplication.utils.getColorByAttribute
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButtonLogin.setOnClickListener {
            val email = binding.loginInputUsername.editText?.text.toString().trim()
            val password = binding.loginInputPassword.editText?.text.toString().trim()

            viewModel.loginClicked(email, password)
        }

        binding.loginButtonSignUp.setOnClickListener {
            val email = binding.loginInputUsername.editText?.text.toString().trim()
            val password = binding.loginInputPassword.editText?.text.toString().trim()

            viewModel.registerClicked(email, password)
        }

        binding.loginTextForgotPassword.setOnClickListener {
            viewModel.forgotPasswordClicked()
        }

        viewModel.state.onEach { state ->
            Timber.d(state.toString())
            binding.loginInputUsername.apply {
                error = if (state.isEmailValid) {
                    isErrorEnabled = false
                    null
                } else {
                    getString(R.string.login_invalid_email)
                }
            }

            binding.loginInputPassword.apply {
                error = if (state.isPasswordValid) {
                    isErrorEnabled = false
                    null
                } else {
                    getString(R.string.login_invalid_password)
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.error.onEach { error ->
            Timber.d("error")
            val messageRes = when (error) {
                LoginErrorType.LOGIN -> R.string.login_failed
                LoginErrorType.SIGNUP -> R.string.login_register_failed
                LoginErrorType.FORGOT_PASSWORD -> R.string.login_forgot_password_failed
            }

            showSnackbar(
                resources.getString(messageRes),
                requireContext().getColorByAttribute(R.attr.colorError)
            )
        }.launchIn(lifecycleScope)

        viewModel.registerSuccess.onEach {
            Timber.d("register success")
            showSnackbar(
                resources.getString(R.string.login_register_success),
                requireContext().getColorByAttribute(R.attr.colorPrimary),
                object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        viewModel.onRegistrationSnackbarDismissed()
                    }
                }
            )
        }.launchIn(lifecycleScope)

        viewModel.bottomSheetShow.onEach {
            Timber.d("showing forgot password dialog")
            showForgotPasswordBottomSheetDialog()
        }.launchIn(lifecycleScope)

        viewModel.forgotPasswordGetSuccess.onEach { password ->
            Timber.d("get password success")
            showSnackbar(
                resources.getString(R.string.login_your_password_is, password),
                requireContext().getColorByAttribute(R.attr.colorOnSecondary)
            )
        }.launchIn(lifecycleScope)

        viewModel.navigateToApp.onEach {
            Timber.d("navigating to logged in screen")
            findNavController().navigate(R.id.action_nav_to_logged_in)
        }.launchIn(lifecycleScope)

    }

    private fun showSnackbar(
        message: String, backgroundTint: Int,
        callback: BaseTransientBottomBar.BaseCallback<Snackbar>? = null
    ) {
        val snackbar = Snackbar
            .make(
                requireContext(),
                binding.root,
                message,
                Snackbar.LENGTH_LONG
            )
            .setBackgroundTint(backgroundTint)

        callback?.let { snackbar.addCallback(it) }
        snackbar.show()
    }

    private fun showForgotPasswordBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val forgotPasswordBinding = ForgotPasswordDialogBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(forgotPasswordBinding.root)

        forgotPasswordBinding.forgotPassButtonSubmit.setOnClickListener {
            val email =
                forgotPasswordBinding.forgotPassInputUsername.editText?.text.toString().trim()
            viewModel.forgotPasswordSubmitClicked(email)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}