package com.raithabharosa.hub.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raithabharosa.hub.TranslationManager
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.ui.theme.GreenGo
import com.raithabharosa.hub.ui.theme.OrangeCaution
import com.raithabharosa.hub.ui.theme.RedWait

@Composable
fun SettingsScreen(
    repository: FarmerRepository,
    showKannadaLabels: Boolean = false,
    onThemeToggle: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val viewModel = viewModel { SettingsViewModel(repository) }
    val state by viewModel.state.collectAsState()
    val t = { text: String -> TranslationManager.translate(text, showKannadaLabels) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val isDarkTheme = remember { com.raithabharosa.hub.ThemeManager.isDarkTheme(context) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = t("Settings"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = t("Back"))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ProfileSection(
                name = state.farmerName,
                location = state.farmerLocation,
                cropType = state.cropType,
                onEditClick = { showEditProfileDialog = true },
                t = t
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SettingsSection(title = t("Preferences")) {
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = t("Dark Mode"),
                    subtitle = if (isDarkTheme) t("Enabled") else t("Disabled"),
                    trailing = {
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onThemeToggle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GreenGo,
                                checkedTrackColor = GreenGo.copy(alpha = 0.5f)
                            )
                        )
                    },
                    t = t
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SettingsSection(title = t("Account")) {
                SettingsItem(
                    icon = Icons.Default.Edit,
                    title = t("Edit Profile"),
                    subtitle = t("Update your name and crop details"),
                    onClick = { showEditProfileDialog = true },
                    t = t
                )
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = t("Update Location"),
                    subtitle = state.farmerLocation.ifEmpty { t("Not set") },
                    onClick = { showLocationDialog = true },
                    t = t
                )
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = t("Logout"),
                    subtitle = t("Sign out of your account"),
                    onClick = { showLogoutDialog = true },
                    isDestructive = true,
                    t = t
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SettingsSection(title = t("About")) {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = t("App Info"),
                    subtitle = "Developed by Kushal J",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Kushalkush-dev/"))
                        context.startActivity(intent)
                    },
                    t = t
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = state.farmerName,
            currentCrop = state.cropType,
            onSave = { name, crop ->
                viewModel.updateProfile(name, crop)
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false },
            t = t
        )
    }

    if (showLocationDialog) {
        LocationDialog(
            currentLocation = state.farmerLocation,
            onSave = { location ->
                viewModel.updateLocation(location)
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false },
            t = t
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(t("Logout")) },
            text = { Text(t("Are you sure you want to logout?")) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text(t("Logout"), color = RedWait)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }
}

@Composable
private fun ProfileSection(
    name: String,
    location: String,
    cropType: String,
    onEditClick: () -> Unit,
    t: (String) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GreenGo.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(GreenGo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name.ifEmpty { t("Farmer") },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = t(cropType.ifEmpty { "Select Crop" }),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (location.isNotEmpty()) {
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = t("Edit"), tint = GreenGo)
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    isDestructive: Boolean = false,
    t: (String) -> String
) {
    val iconColor = if (isDestructive) RedWait else OrangeCaution

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) RedWait else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    currentName: String,
    currentCrop: String,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit,
    t: (String) -> String
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedCrop by remember { mutableStateOf(currentCrop.ifEmpty { "Sugarcane" }) }
    var showCropPicker by remember { mutableStateOf(false) }

    val crops = listOf("Sugarcane", "Ragi", "Paddy", "Wheat", "Maize")

    if (showCropPicker) {
        AlertDialog(
            onDismissRequest = { showCropPicker = false },
            title = { Text(t("Select Crop")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    crops.forEach { crop ->
                        Card(
                            onClick = {
                                selectedCrop = crop
                                showCropPicker = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCrop == crop) GreenGo.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(t(crop))
                                if (selectedCrop == crop) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = GreenGo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCropPicker = false }) {
                    Text(t("Done"))
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(t("Edit Profile")) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(t("Name")) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(t("Crop Type"), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        onClick = { showCropPicker = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(t(selectedCrop))
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onSave(name, selectedCrop) },
                    enabled = name.isNotBlank()
                ) {
                    Text(t("Save"), color = GreenGo)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(t("Cancel"))
                }
            }
        )
    }
}

@Composable
private fun LocationDialog(
    currentLocation: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    t: (String) -> String
) {
    var location by remember { mutableStateOf(currentLocation) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t("Update Location")) },
        text = {
            Column {
                Text(
                    text = t("Enter your village/taluk location"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(t("Location")) },
                    placeholder = { Text("e.g., Mysore, Karnataka") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(location) }) {
                Text(t("Save"), color = GreenGo)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t("Cancel"))
            }
        }
    )
}