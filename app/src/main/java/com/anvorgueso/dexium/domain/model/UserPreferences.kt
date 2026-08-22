package com.anvorgueso.dexium.domain.model

import com.anvorgueso.dexium.core.util.AppLanguage

data class UserPreferences(
    val isOnboardingComplete: Boolean = false,
    val useHdImages: Boolean = true,
    val useImperialUnits: Boolean = false,
    /**
     * null means "follow the device". Stored rather than resolved so that changing the phone's
     * language keeps moving the app until the user makes an explicit choice.
     */
    val languageCode: String? = null
) {
    val language: AppLanguage
        get() = AppLanguage.fromCode(languageCode) ?: AppLanguage.fromDeviceLocale()

    val isFollowingDevice: Boolean get() = languageCode == null
}
