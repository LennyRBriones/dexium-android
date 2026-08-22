package com.anvorgueso.dexium.ui.components

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Swaps only the resources of [base], keeping everything else — crucially the Activity that
 * sits at the bottom of the ContextWrapper chain.
 *
 * `createConfigurationContext` alone returns a bare ContextImpl, and `hiltViewModel()` walks
 * the wrapper chain looking for an Activity; handing it that context throws
 * "Expected an activity context for creating a HiltViewModelFactory".
 */
private class LocalizedResourcesContext(
    base: Context,
    private val localizedResources: Resources
) : ContextWrapper(base) {
    override fun getResources(): Resources = localizedResources
    override fun getAssets(): AssetManager = localizedResources.assets
}

/**
 * Renders [content] with resources resolved against [languageTag] instead of the device
 * locale, so every `stringResource` inside picks up the user's chosen language.
 *
 * Done at the Compose layer on purpose. `AppCompatDelegate.setApplicationLocales` would mean
 * adding appcompat and moving off ComponentActivity, and `LocaleManager` needs API 33 while
 * this app supports 30. Since the whole UI is Compose, overriding the two composition locals
 * that `stringResource` reads is enough and costs no dependency.
 */
@Composable
fun ProvideAppLocale(
    languageTag: String,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val localizedContext = remember(languageTag, configuration) {
        val locale = Locale.forLanguageTag(languageTag)
        val localizedConfig = Configuration(configuration).apply {
            setLocales(LocaleList(locale))
        }
        LocalizedResourcesContext(
            base = context,
            localizedResources = context.createConfigurationContext(localizedConfig).resources
        )
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration,
        content = content
    )
}
