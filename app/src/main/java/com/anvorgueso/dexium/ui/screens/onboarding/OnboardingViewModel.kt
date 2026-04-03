package com.anvorgueso.dexium.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _useHdImages = MutableStateFlow(true)
    val useHdImages: StateFlow<Boolean> = _useHdImages

    private val _navigateToHome = MutableSharedFlow<Unit>()
    val navigateToHome: SharedFlow<Unit> = _navigateToHome

    fun selectImageQuality(useHd: Boolean) {
        _useHdImages.value = useHd
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setUseHdImages(_useHdImages.value)
            userPreferencesRepository.setOnboardingComplete(true)
            _navigateToHome.emit(Unit)
        }
    }
}
