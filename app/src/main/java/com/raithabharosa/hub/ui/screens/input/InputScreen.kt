package com.raithabharosa.hub.ui.screens.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raithabharosa.hub.TranslationManager
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.ui.theme.GreenGo
import com.raithabharosa.hub.ui.theme.RedWait

@Composable
fun InputScreen(
    repository: FarmerRepository,
    showKannadaLabels: Boolean = false
) {
    val viewModel = viewModel { InputViewModel(repository) }
    val state by viewModel.state.collectAsState()

    val t = { text: String -> TranslationManager.translate(text, showKannadaLabels) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = t("Soil Input Center"),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = t("Enter soil test results to get accurate recommendations"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SoilInputField(
            label = t("Soil Moisture (%)"),
            value = state.moisture,
            onValueChange = { viewModel.updateMoisture(it) },
            placeholder = t("e.g., 25")
        )

        Spacer(modifier = Modifier.height(12.dp))

        SoilInputField(
            label = t("Nitrogen - N (kg/ha)"),
            value = state.nitrogen,
            onValueChange = { viewModel.updateNitrogen(it) },
            placeholder = t("e.g., 60")
        )

        Spacer(modifier = Modifier.height(12.dp))

        SoilInputField(
            label = t("Phosphorus - P (kg/ha)"),
            value = state.phosphorus,
            onValueChange = { viewModel.updatePhosphorus(it) },
            placeholder = t("e.g., 30")
        )

        Spacer(modifier = Modifier.height(12.dp))

        SoilInputField(
            label = t("Potassium - K (kg/ha)"),
            value = state.potassium,
            onValueChange = { viewModel.updatePotassium(it) },
            placeholder = t("e.g., 25")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.saveSoilData() },
                enabled = viewModel.isValid(),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenGo)
            ) {
                Text(
                    text = if (state.isSaved) t("Saved!") else t("Save"),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedButton(
                onClick = { viewModel.clearSoilData() },
                modifier = Modifier.height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedWait)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(t("Clear"), fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = t("Note: Leave fields empty to use default values. Clear resets all values."),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SoilInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
    }
}