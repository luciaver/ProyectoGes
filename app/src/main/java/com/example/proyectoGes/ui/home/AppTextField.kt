package com.example.proyectoGes.ui.home

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Componente reutilizable de TextField con el estilo oscuro/azul de la app
@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardColor,
            unfocusedContainerColor = CardColor,
            focusedBorderColor = BlueLight,
            unfocusedBorderColor = TextSecondary,
            focusedTextColor = White,
            unfocusedTextColor = White,
            cursorColor = BlueLight
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    )
}