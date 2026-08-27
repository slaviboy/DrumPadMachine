package com.slaviboy.drumpadmachine.screens.settings.composables

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.ScrollableContainer
import com.slaviboy.drumpadmachine.enums.MetronomeSound
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.screens.settings.viewmodels.SettingsViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont

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

    ScrollableContainer(
        minHeight = 0.19.dw,
        maxHeight = 0.32.dw,
        topBar = { height, minHeight, maxHeight ->
            SettingsTopBar(
                height = height,
                minHeight = minHeight,
                maxHeight = maxHeight,
                title = stringResource(id = R.string.settings),
                onLeftButtonClicked = { navigator.navigateUp() }
            )
        }
    ) { _, topBarOffset ->
        item {
            Spacer(modifier = Modifier.height(0.02.dw + topBarOffset))
        }
        item {
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_app))
        }
        item {
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
        }

        item { Spacer(modifier = Modifier.height(0.05.dw)) }

        item {
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_audio))
        }
        item {
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
        }

        item { Spacer(modifier = Modifier.height(0.05.dw)) }

        item {
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_data))
        }
        item {
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
        }

        item { Spacer(modifier = Modifier.height(0.05.dw)) }

        item {
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_other))
        }
        item {
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
        }

        item { Spacer(modifier = Modifier.height(0.05.dw)) }

        item {
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_about))
        }

        item {
            SettingsCard {
                SettingsAppInfoRow(appVersion = appVersion)
            }
        }

        item { Spacer(modifier = Modifier.height(0.05.dw)) }

        item {
            ResetSettingsButton(onClick = { showResetConfirmation = true })
        }

        item { Spacer(modifier = Modifier.height(0.05.dw)) }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
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
            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(0.09.dw)
            )
        }
    }

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
}

private fun formatCacheSize(bytes: Long): String {
    val megabytes = bytes / (1024.0 * 1024.0)
    return "%.1f MB".format(megabytes)
}
