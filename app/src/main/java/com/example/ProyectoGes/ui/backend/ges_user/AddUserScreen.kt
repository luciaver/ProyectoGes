package com.example.ProyectoGes.ui.backend.ges_user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ProyectoGes.models.User
import com.example.ProyectoGes.models.UserRoles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: GesUserViewModel = viewModel(
        factory = GesUserViewModelFactory(context)
    )

    var nombre       by rememberSaveable { mutableStateOf("") }
    var email        by rememberSaveable { mutableStateOf("") }
    var password     by rememberSaveable { mutableStateOf("") }
    var edad         by rememberSaveable { mutableStateOf("") }
    var telefono     by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf("JUGADOR") }
    var posicion     by rememberSaveable { mutableStateOf("Portero") }
    var equipo       by rememberSaveable { mutableStateOf("Sin equipo") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }


    var posicionExpanded by rememberSaveable { mutableStateOf(false) }
    var equipoExpanded   by rememberSaveable { mutableStateOf(false) }


    val redPrimary      = Color(0xFFFF0000)
    val grayBackground  = Color(0xFFE0E0E0)
    val whiteCard       = Color(0xFFFFFFFF)

    // Si el rol cambia de JUGADOR, limpiar posición y equipo
    val esJugador = selectedRole == "JUGADOR"

    Scaffold(
        containerColor = grayBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text("Añadir Usuario", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = redPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            //  Nombre
            FieldLabel("Nombre completo")
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Introduce el nombre") },
                singleLine = true,
                colors = fieldColors(redPrimary, whiteCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            FieldLabel("Email")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Introduce el email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = fieldColors(redPrimary, whiteCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            //  Contraseña
            FieldLabel("Contraseña")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Introduce la contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = fieldColors(redPrimary, whiteCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Edad
            FieldLabel("Edad")
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it.filter { c -> c.isDigit() } },
                placeholder = { Text("Ej: 25") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = fieldColors(redPrimary, whiteCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Teléfono
            FieldLabel("Teléfono")
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = { Text("Ej: 612 345 678") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                colors = fieldColors(redPrimary, whiteCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de Rol
            FieldLabel("Selecciona el rol:")
            Spacer(modifier = Modifier.height(4.dp))

            UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedRole == roleKey,
                        onClick = {
                            selectedRole = roleKey
                            // Si sale de JUGADOR, resetear posición y equipo
                            if (roleKey != "JUGADOR") {
                                posicion = "Portero"
                                equipo   = "Sin equipo"
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor   = redPrimary,
                            unselectedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = roleLabel, color = Color.Black, fontSize = 16.sp)
                }
            }

            if (esJugador) {
                Spacer(modifier = Modifier.height(24.dp))

                //  Posición
                FieldLabel("Posición")
                ExposedDropdownMenuBox(
                    expanded = posicionExpanded,
                    onExpandedChange = { posicionExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = posicion,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posicionExpanded) },
                        colors = fieldColors(redPrimary, whiteCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = posicionExpanded,
                        onDismissRequest = { posicionExpanded = false }
                    ) {
                        UserRoles.posiciones.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos) },
                                onClick = {
                                    posicion = pos
                                    posicionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Equipo
                FieldLabel("Pertenencia a un equipo")
                ExposedDropdownMenuBox(
                    expanded = equipoExpanded,
                    onExpandedChange = { equipoExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = equipo,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = equipoExpanded) },
                        colors = fieldColors(redPrimary, whiteCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = equipoExpanded,
                        onDismissRequest = { equipoExpanded = false }
                    ) {
                        UserRoles.equipos.forEach { eq ->
                            DropdownMenuItem(
                                text = { Text(eq) },
                                onClick = {
                                    equipo = eq
                                    equipoExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Guardar
            Button(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() -> {
                            errorMessage = "Nombre, email y contraseña son obligatorios"
                        }
                        edad.isBlank() -> {
                            errorMessage = "La edad es obligatoria"
                        }
                        edad.toIntOrNull() == null || edad.toInt() < 1 || edad.toInt() > 120 -> {
                            errorMessage = "Introduce una edad válida (1–120)"
                        }
                        telefono.isBlank() -> {
                            errorMessage = "El teléfono es obligatorio"
                        }
                        else -> {
                            errorMessage = null
                            val newUser = User(
                                id       = 0,
                                nombre   = nombre,
                                email    = email,
                                password = password,
                                rol      = selectedRole,
                                edad     = edad.toInt(),
                                telefono = telefono,
                                posicion = if (esJugador) posicion else null,
                                equipo   = if (esJugador) equipo  else null
                            )
                            viewModel.addUser(newUser)
                            navController.popBackStack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Guardar Usuario", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }


            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage!!, color = redPrimary)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun fieldColors(redPrimary: Color, whiteCard: Color) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor   = whiteCard,
    unfocusedContainerColor = whiteCard,
    focusedBorderColor      = redPrimary,
    unfocusedBorderColor    = Color.Gray,
    cursorColor             = redPrimary
)