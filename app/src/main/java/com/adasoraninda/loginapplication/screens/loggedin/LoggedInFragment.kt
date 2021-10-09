package com.adasoraninda.loginapplication.screens.loggedin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adasoraninda.loginapplication.R
import com.adasoraninda.loginapplication.databinding.FragmentLoggedInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class LoggedInFragment : Fragment() {

    private var _binding: FragmentLoggedInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoggedInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoggedInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        observeViewModel()
    }

    private fun setup() {
        binding.loggedInToolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_logout) {
                viewModel.logOutClicked()
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.email.onEach { email ->
            Timber.d(email)
            binding.loggedInTextWelcome.text = resources.getString(R.string.logged_in_welcome, email)
        }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}