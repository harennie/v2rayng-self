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
    primary = Color(0xFF1F3A34),
    onPrimary = Color(0xFFF7F3EA),
    primaryContainer = Color(0xFFD5E4DC),
    onPrimaryContainer = Color(0xFF0E241F),
    secondary = Color(0xFF8C4A2F),
    onSecondary = Color(0xFFFFF7F2),
    secondaryContainer = Color(0xFFF3D7C6),
    onSecondaryContainer = Color(0xFF3A1C0E),
    tertiary = Color(0xFF2F6F5E),
    onTertiary = Color(0xFFF3FBF7),
    tertiaryContainer = Color(0xFFD4EFE4),
    onTertiaryContainer = Color(0xFF0C2921),
    error = Color(0xFFB4234A),
    errorContainer = Color(0xFFFFD9E1),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF3F0014),
    background = Color(0xFFF4F1EA),
    onBackground = Color(0xFF1C1915),
    surface = Color(0xFFF7F4EE),
    onSurface = Color(0xFF1C1915),
    surfaceVariant = Color(0xFFE6E0D4),
    onSurfaceVariant = Color(0xFF564E45),
    outline = Color(0xFF8A8176),
    outlineVariant = Color(0xFFD9D2C5),
    inverseSurface = Color(0xFF2C2925),
    inverseOnSurface = Color(0xFFF4F1EA),
    inversePrimary = Color(0xFFB7D2C6),
    scrim = Color(0xFF1C1915),
    surfaceTint = Color(0xFF1F3A34),
    surfaceContainerLowest = Color(0xFFFFFCF7),
    surfaceContainerLow = Color(0xFFF7F4EE),
    surfaceContainer = Color(0xFFEFEBE3),
    surfaceContainerHigh = Color(0xFFE7E1D6),
    surfaceContainerHighest = Color(0xFFE0D9CC),
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFD7CBB8),
    onPrimary = Color(0xFF1C1915),
    primaryContainer = Color(0xFF314940),
    onPrimaryContainer = Color(0xFFE4F1EA),
    secondary = Color(0xFFE7B08A),
    onSecondary = Color(0xFF3A1C0E),
    secondaryContainer = Color(0xFF5C3826),
    onSecondaryContainer = Color(0xFFFFE8D8),
    tertiary = Color(0xFF9ED4C2),
    onTertiary = Color(0xFF0C2921),
    tertiaryContainer = Color(0xFF1E4A3E),
    onTertiaryContainer = Color(0xFFD4EFE4),
    error = Color(0xFFFFB3C1),
    errorContainer = Color(0xFF8E1736),
    onError = Color(0xFF3F0014),
    onErrorContainer = Color(0xFFFFD9E1),
    background = Color(0xFF121411),
    onBackground = Color(0xFFE7E2D6),
    surface = Color(0xFF171A17),
    onSurface = Color(0xFFE7E2D6),
    surfaceVariant = Color(0xFF3A3832),
    onSurfaceVariant = Color(0xFFD0C8BA),
    outline = Color(0xFF9A9286),
    outlineVariant = Color(0xFF3F3C36),
    inverseSurface = Color(0xFFE7E2D6),
    inverseOnSurface = Color(0xFF1C1915),
    inversePrimary = Color(0xFF1F3A34),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFD7CBB8),
    surfaceContainerLowest = Color(0xFF0C0E0C),
    surfaceContainerLow = Color(0xFF171A17),
    surfaceContainer = Color(0xFF1E211E),
    surfaceContainerHigh = Color(0xFF282B28),
    surfaceContainerHighest = Color(0xFF333632),
)

// Semantic Colors
val colorPing = Color(0xFF2F6F5E)
val colorPingRed = Color(0xFFB4234A)
val colorConfigType = Color(0xFF8C4A2F)
val colorFabActive = Color(0xFF1F3A34)
val colorFabInactiveLight = Color(0xFF8A8176)
val colorFabInactiveDark = Color(0xFF5C564E)
val dividerColorLight = Color(0xFFD9D2C5)
val dividerColorDark = Color(0xFF3F3C36)

// Toast Colors 85%
val toastNormalBgLight = Color(0xD9353A3E) // Dark Gray
val toastNormalBgDark = Color(0xD94A4F54) // Darker Gray
val toastSuccessBg = Color(0xD9388E3C) // Green
val toastErrorBg = Color(0xD9D50000) // Red
val toastInfoBg = Color(0xD93F51B5) // Indigo Blue
val toastIconCircleBg = Color(0x33FFFFFF) // Semi-transparent White
val toastTextColor = Color.White // White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
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
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
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
