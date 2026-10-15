package com.v2ray.ang.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val LightColor = lightColorScheme(
    primary = Color(0xFFB01030),
    onPrimary = Color(0xFFFFF8F6),
    primaryContainer = Color(0xFFFFD9DF),
    onPrimaryContainer = Color(0xFF5C0016),
    secondary = Color(0xFF8A5A00),
    onSecondary = Color(0xFFFFF8EE),
    secondaryContainer = Color(0xFFFFE08A),
    onSecondaryContainer = Color(0xFF3D2800),
    tertiary = Color(0xFF8E3048),
    onTertiary = Color(0xFFFFF7F8),
    tertiaryContainer = Color(0xFFF8D0DA),
    onTertiaryContainer = Color(0xFF4A1020),
    error = Color(0xFF9F1239),
    errorContainer = Color(0xFFFFD9E1),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF3F0014),
    background = Color(0xFFFFF6F7),
    onBackground = Color(0xFF1A1214),
    surface = Color(0xFFFFFBFB),
    onSurface = Color(0xFF1A1214),
    surfaceVariant = Color(0xFFF3E0E4),
    onSurfaceVariant = Color(0xFF5C4048),
    outline = Color(0xFF8A6670),
    outlineVariant = Color(0xFFE4C8CE),
    inverseSurface = Color(0xFF2A1A1E),
    inverseOnSurface = Color(0xFFFFF1F3),
    inversePrimary = Color(0xFFFFB3C0),
    scrim = Color(0xFF14080C),
    surfaceTint = Color(0xFFB01030),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF6F7),
    surfaceContainer = Color(0xFFFFEEF1),
    surfaceContainerHigh = Color(0xFFFCE4E8),
    surfaceContainerHighest = Color(0xFFF6D6DC),
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFFF4D6A),
    onPrimary = Color(0xFF3F0012),
    primaryContainer = Color(0xFF8E1028),
    onPrimaryContainer = Color(0xFFFFD9E0),
    secondary = Color(0xFFFFD56A),
    onSecondary = Color(0xFF3D2800),
    secondaryContainer = Color(0xFF6B4A10),
    onSecondaryContainer = Color(0xFFFFE9B0),
    tertiary = Color(0xFFFFB0C4),
    onTertiary = Color(0xFF4A1024),
    tertiaryContainer = Color(0xFF6E3044),
    onTertiaryContainer = Color(0xFFFFD9E4),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF12080C),
    onBackground = Color(0xFFFFF1F3),
    surface = Color(0xFF1A0E12),
    onSurface = Color(0xFFFFF1F3),
    surfaceVariant = Color(0xFF3A242A),
    onSurfaceVariant = Color(0xFFE8C8D0),
    outline = Color(0xFFB08A94),
    outlineVariant = Color(0xFF4A3036),
    inverseSurface = Color(0xFFFFF1F3),
    inverseOnSurface = Color(0xFF2A161C),
    inversePrimary = Color(0xFFB01030),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFF4D6A),
    surfaceContainerLowest = Color(0xFF0C0608),
    surfaceContainerLow = Color(0xFF1A0E12),
    surfaceContainer = Color(0xFF221218),
    surfaceContainerHigh = Color(0xFF2E1A20),
    surfaceContainerHighest = Color(0xFF3A2228),
)

// Semantic Colors
val colorPing = Color(0xFF8A5A00)
val colorPingDark = Color(0xFFFFD56A)
val colorPingRed = Color(0xFFB01030)
val colorPingRedDark = Color(0xFFFF6B86)
val colorConfigType = Color(0xFF8A5A00)
val colorConfigTypeDark = Color(0xFFFFD56A)
val colorFabActive = Color(0xFFB01030)
val colorFabInactiveLight = Color(0xFF8A6670)
val colorFabInactiveDark = Color(0xFF5C4048)
val dividerColorLight = Color(0xFFE4C8CE)
val dividerColorDark = Color(0xFF4A3036)

// Toast Colors 85%
val toastNormalBgLight = Color(0xD91A1214)
val toastNormalBgDark = Color(0xD92A161C)
val toastSuccessBg = Color(0xD9A56B00)
val toastErrorBg = Color(0xD9B01030)
val toastInfoBg = Color(0xD96E3044)
val toastIconCircleBg = Color(0x33FFFFFF) // Semi-transparent White
val toastTextColor = Color.White // White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor by ThemeManager.dynamicColorEnabled.collectAsState()
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColor
        else -> LightColor
    }
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
