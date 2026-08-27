package com.slaviboy.drumpadmachine.screens.settings.composables

import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.NumberStepper
import com.slaviboy.drumpadmachine.enums.MetronomeSound
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.screens.settings.viewmodels.SettingsViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop
import kotlin.math.roundToInt

private val sliderAccentColor = Color(0xFFffd112)
private val badgeHaptic = Color(0xFFE85D9C)
private val badgeScreen = Color(0xFF3FC7C7)
private val badgeMetronome = Color(0xFF4CC98A)
private val badgeBpm = Color(0xFFF2A93B)
private val badgeCache = Color(0xFFE0574A)
private val badgeBackup = Color(0xFF5B8DEF)
private val dangerColor = Color(0xFFFF5A5A)
private val dialogSurfaceColor = Color(0xFF2D2D42)

@RootNavGraph(start = false)
@Destination
@Composable
fun SettingsComposable(
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val appVersion = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }
    var showResetConfirmation by rememberSaveable { mutableStateOf(false) }
    var showMetronomeSoundPicker by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        backgroundGradientTop,
                        backgroundGradientBottom
                    )
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(
            modifier = Modifier
                .height(0.07.dw)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .size(0.07.dw)
                .offset(x = 0.04.dw)
                .bounceClick {
                    navigator.navigateUp()
                },
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(
            modifier = Modifier
                .height(0.05.dw)
        )
        Text(
            text = stringResource(id = R.string.settings),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.063.sw,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 0.06.dw)
        )
        Spacer(
            modifier = Modifier
                .height(0.06.dw)
        )

        SettingsSectionHeader(text = stringResource(id = R.string.settings_section_app))
        SettingsCard {
            SettingsToggleRow(
                icon = Icons.Filled.Vibration,
                iconTint = badgeHaptic,
                titleResId = R.string.haptic_feedback,
                subtitleResId = R.string.haptic_feedback_subtitle,
                checked = settingsViewModel.hapticFeedback.value,
                onCheckedChange = { settingsViewModel.setHapticFeedback(it) }
            )
            SettingsToggleRow(
                icon = Icons.Filled.Smartphone,
                iconTint = badgeScreen,
                titleResId = R.string.keep_screen_on,
                subtitleResId = R.string.keep_screen_on_subtitle,
                checked = settingsViewModel.keepScreenOn.value,
                onCheckedChange = { settingsViewModel.setKeepScreenOn(it) }
            )
        }

        Spacer(modifier = Modifier.height(0.05.dw))

        SettingsSectionHeader(text = stringResource(id = R.string.settings_section_audio))
        SettingsCard {
            SettingsSliderRow(
                icon = Icons.Filled.SwapHoriz,
                iconTint = sliderAccentColor,
                titleResId = R.string.pan,
                value = settingsViewModel.pan.value,
                valueRange = -100f..100f,
                valueText = "${settingsViewModel.pan.value}",
                onValueChange = { settingsViewModel.setPan(it) }
            )
            SettingsSliderRow(
                icon = Icons.Filled.GraphicEq,
                iconTint = sliderAccentColor,
                titleResId = R.string.reverb,
                value = settingsViewModel.reverb.value,
                valueRange = 0f..100f,
                valueText = "${settingsViewModel.reverb.value}%",
                onValueChange = { settingsViewModel.setReverb(it) }
            )
            SettingsSliderRow(
                icon = Icons.Filled.VolumeUp,
                iconTint = sliderAccentColor,
                titleResId = R.string.volume,
                value = settingsViewModel.volume.value,
                valueRange = 0f..150f,
                valueText = "${settingsViewModel.volume.value}%",
                onValueChange = { settingsViewModel.setVolume(it) }
            )
            SettingsSliderRow(
                icon = Icons.Filled.Headset,
                iconTint = sliderAccentColor,
                titleResId = R.string.metronome_volume,
                value = settingsViewModel.metronomeVolume.value,
                valueRange = 0f..100f,
                valueText = "${settingsViewModel.metronomeVolume.value}%",
                onValueChange = { settingsViewModel.setMetronomeVolume(it) }
            )
            SettingsToggleRow(
                iconResId = R.drawable.ic_metronome,
                iconTint = badgeMetronome,
                titleResId = R.string.metronome,
                subtitleResId = R.string.metronome_subtitle,
                checked = settingsViewModel.metronomeEnabled.value,
                onCheckedChange = { settingsViewModel.setMetronomeEnabled(it) }
            )
            SettingsActionRow(
                icon = Icons.Filled.MusicNote,
                iconTint = badgeMetronome,
                titleResId = R.string.metronome_sound,
                subtitleResId = R.string.metronome_sound_subtitle,
                trailingText = stringResource(id = settingsViewModel.metronomeSound.value.labelResId),
                onClick = { showMetronomeSoundPicker = true }
            )
            SettingsStepperRow(
                icon = Icons.Filled.Timer,
                iconTint = badgeBpm,
                titleResId = R.string.default_bpm,
                subtitleResId = R.string.default_bpm_subtitle,
                value = settingsViewModel.defaultBpm.value,
                range = 40..240,
                step = 1,
                onValueChange = { settingsViewModel.setDefaultBpm(it) }
            )
        }

        Spacer(modifier = Modifier.height(0.05.dw))

        SettingsSectionHeader(text = stringResource(id = R.string.settings_section_data))
        SettingsCard {
            SettingsActionRow(
                icon = Icons.Filled.Delete,
                iconTint = badgeCache,
                titleResId = R.string.clear_cache,
                subtitleResId = R.string.clear_cache_subtitle,
                trailingText = formatCacheSize(settingsViewModel.cacheSizeBytes.value),
                onClick = { settingsViewModel.clearCache() }
            )
            SettingsActionRow(
                icon = Icons.Filled.CloudUpload,
                iconTint = badgeBackup,
                titleResId = R.string.backup_restore,
                subtitleResId = R.string.backup_restore_subtitle,
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(0.05.dw))

        SettingsSectionHeader(text = stringResource(id = R.string.settings_section_other))
        SettingsCard {
            SettingsActionRow(
                icon = Icons.Filled.Language,
                iconTint = badgeScreen,
                titleResId = R.string.language,
                subtitleResId = R.string.language_subtitle,
                trailingText = "English",
                enabled = false
            )
            SettingsActionRow(
                icon = Icons.Filled.Description,
                iconTint = badgeBackup,
                titleResId = R.string.export_logs,
                subtitleResId = R.string.export_logs_subtitle,
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(0.05.dw))

        SettingsCard {
            SettingsAppInfoRow(appVersion = appVersion)
        }

        Spacer(modifier = Modifier.height(0.05.dw))

        ResetSettingsButton(onClick = { showResetConfirmation = true })

        if (showResetConfirmation) {
            AlertDialog(
                onDismissRequest = { showResetConfirmation = false },
                containerColor = dialogSurfaceColor,
                titleContentColor = Color.White,
                textContentColor = Color.LightGray,
                title = {
                    Text(
                        text = stringResource(id = R.string.reset_settings_confirm_title),
                        fontFamily = RobotoFont,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.reset_settings_confirm_message),
                        fontFamily = RobotoFont
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        settingsViewModel.resetToDefaults()
                        showResetConfirmation = false
                    }) {
                        Text(
                            text = stringResource(id = R.string.reset),
                            color = dangerColor,
                            fontFamily = RobotoFont,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmation = false }) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = Color.White,
                            fontFamily = RobotoFont
                        )
                    }
                }
            )
        }

        if (showMetronomeSoundPicker) {
            AlertDialog(
                onDismissRequest = { showMetronomeSoundPicker = false },
                containerColor = dialogSurfaceColor,
                titleContentColor = Color.White,
                title = {
                    Text(
                        text = stringResource(id = R.string.metronome_sound),
                        fontFamily = RobotoFont,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        MetronomeSound.values().forEach { sound ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bounceClick {
                                        settingsViewModel.setMetronomeSound(sound)
                                        showMetronomeSoundPicker = false
                                    }
                                    .padding(vertical = 0.01.dw),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settingsViewModel.metronomeSound.value == sound,
                                    onClick = {
                                        settingsViewModel.setMetronomeSound(sound)
                                        showMetronomeSoundPicker = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = sliderAccentColor)
                                )
                                Text(
                                    text = stringResource(id = sound.labelResId),
                                    color = Color.White,
                                    fontFamily = RobotoFont
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showMetronomeSoundPicker = false }) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = Color.White,
                            fontFamily = RobotoFont
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(0.05.dw))

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .bounceClick {
                    uriHandler.openUri("https://github.com/slaviboy")
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_github),
                contentDescription = null,
                modifier = Modifier
                    .size(0.1.dw),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(
                modifier = Modifier
                    .width(0.01.dw)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.recreated_by),
                    color = Color.Gray,
                    fontFamily = RobotoFont,
                    fontSize = 0.028.sw,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(horizontal = 0.01.dw)
                )
                Text(
                    text = "Slaviboy",
                    color = Color.White,
                    fontFamily = RobotoFont,
                    fontSize = 0.038.sw,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 0.01.dw)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(0.09.dw)
        )
    }
}

private fun formatCacheSize(bytes: Long): String {
    val megabytes = bytes / (1024.0 * 1024.0)
    return "%.1f MB".format(megabytes)
}

@Composable
private fun SettingsSectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        color = sliderAccentColor,
        fontFamily = RobotoFont,
        fontSize = 0.032.sw,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(horizontal = 0.06.dw, vertical = 0.015.dw)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.045.dw)
            .clip(RoundedCornerShape(0.035.dw))
            .background(Color.White.copy(alpha = 0.06f))
    ) {
        content()
    }
}

@Composable
private fun SettingsIconBadge(tint: Color, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(0.1.dw)
            .clip(RoundedCornerShape(0.025.dw))
            .background(tint.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SettingsRowTexts(titleResId: Int, subtitleResId: Int, enabled: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = titleResId),
            color = if (enabled) Color.White else Color.Gray,
            fontFamily = RobotoFont,
            fontSize = 0.04.sw,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = subtitleResId),
            color = Color.Gray,
            fontFamily = RobotoFont,
            fontSize = 0.032.sw,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.02.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = iconTint) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(0.05.dw))
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Box(modifier = Modifier.weight(1f)) {
            SettingsRowTexts(titleResId = titleResId, subtitleResId = subtitleResId)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = sliderAccentColor,
                checkedTrackColor = sliderAccentColor.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingsToggleRow(
    @DrawableRes iconResId: Int,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.02.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = iconTint) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(0.05.dw),
                colorFilter = ColorFilter.tint(iconTint)
            )
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Box(modifier = Modifier.weight(1f)) {
            SettingsRowTexts(titleResId = titleResId, subtitleResId = subtitleResId)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = sliderAccentColor,
                checkedTrackColor = sliderAccentColor.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingsSliderRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    valueText: String,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.015.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = iconTint) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(0.05.dw))
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Text(
            text = stringResource(id = titleResId),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.036.sw,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(0.24.dw)
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = sliderAccentColor,
                activeTrackColor = sliderAccentColor
            )
        )
        Spacer(modifier = Modifier.width(0.02.dw))
        Text(
            text = valueText,
            color = Color.LightGray,
            fontFamily = RobotoFont,
            fontSize = 0.032.sw,
            textAlign = TextAlign.End,
            modifier = Modifier.width(0.13.dw)
        )
    }
}

@Composable
private fun SettingsStepperRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    value: Int,
    range: IntRange,
    step: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.02.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = iconTint) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(0.05.dw))
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Box(modifier = Modifier.weight(1f)) {
            SettingsRowTexts(titleResId = titleResId, subtitleResId = subtitleResId)
        }
        NumberStepper(
            value = value,
            range = range,
            step = step,
            accentColor = sliderAccentColor,
            onValueChange = onValueChange
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    trailingText: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (enabled) it.bounceClick(onClick = onClick) else it }
            .padding(horizontal = 0.04.dw, vertical = 0.02.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = if (enabled) iconTint else Color.Gray) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) iconTint else Color.Gray,
                modifier = Modifier.size(0.05.dw)
            )
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Box(modifier = Modifier.weight(1f)) {
            SettingsRowTexts(titleResId = titleResId, subtitleResId = subtitleResId, enabled = enabled)
        }
        trailingText?.let {
            Text(
                text = it,
                color = if (enabled) sliderAccentColor else Color.Gray,
                fontFamily = RobotoFont,
                fontSize = 0.034.sw,
                modifier = Modifier.padding(end = 0.01.dw)
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(0.055.dw)
        )
    }
}

@Composable
private fun AppIconGrid() {
    Column(
        modifier = Modifier
            .size(0.1.dw)
            .clip(RoundedCornerShape(0.025.dw))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(0.002.dw),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.9f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun SettingsAppInfoRow(appVersion: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.025.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconGrid()
        Spacer(modifier = Modifier.width(0.03.dw))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.app_display_name),
                color = Color.White,
                fontFamily = RobotoFont,
                fontSize = 0.04.sw,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.app_version, appVersion),
                color = Color.LightGray,
                fontFamily = RobotoFont,
                fontSize = 0.032.sw,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = stringResource(id = R.string.app_tagline),
                color = Color.Gray,
                fontFamily = RobotoFont,
                fontSize = 0.032.sw,
                fontWeight = FontWeight.Normal
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(0.055.dw)
        )
    }
}

@Composable
private fun ResetSettingsButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.045.dw)
            .clip(RoundedCornerShape(0.035.dw))
            .background(dangerColor.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = dangerColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(0.035.dw)
            )
            .bounceClick(onClick = onClick)
            .padding(vertical = 0.03.dw),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Restore,
            contentDescription = null,
            tint = dangerColor,
            modifier = Modifier.size(0.045.dw)
        )
        Spacer(modifier = Modifier.width(0.02.dw))
        Text(
            text = stringResource(id = R.string.reset_to_defaults),
            color = dangerColor,
            fontFamily = RobotoFont,
            fontSize = 0.04.sw,
            fontWeight = FontWeight.Bold
        )
    }
}