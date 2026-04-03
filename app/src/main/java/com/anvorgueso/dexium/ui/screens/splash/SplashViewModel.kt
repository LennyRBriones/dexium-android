package com.anvorgueso.dexium.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashNavigationEvent {
    data object GoToOnboarding : SplashNavigationEvent()
    data object GoToHome : SplashNavigationEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent

    fun checkOnboardingStatus() {
        viewModelScope.launch {
            val preferences = userPreferencesRepository.userPreferences.first()
            if (preferences.isOnboardingComplete) {
                _navigationEvent.emit(SplashNavigationEvent.GoToHome)
            } else {
                _navigationEvent.emit(SplashNavigationEvent.GoToOnboarding)
            }
        }
    }
}
