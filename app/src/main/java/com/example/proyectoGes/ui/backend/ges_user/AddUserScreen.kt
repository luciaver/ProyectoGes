package com.example.proyectoGes.ui.backend.ges_user

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proyectoGes.models.User
import com.example.proyectoGes.models.UserRoles
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModel
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModelFactory
import com.example.proyectoGes.ui.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavHostController) {
    val context   = LocalContext.current
    val viewModel: GesUserViewModel = viewModel(factory = GesUserViewModelFactory(context))
    val teamVM: TeamViewModel       = viewModel(factory = TeamViewModelFactory(context))
    val teams by teamVM.teams.collectAsState()

    var nombre       by rememberSaveable { mutableStateOf("") }
    var email        by rememberSaveable { mutableStateOf("") }
    var password     by rememberSaveable { mutableStateOf("") }
    var edad         by rememberSaveable { mutableStateOf("") }
    var telefono     by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf("JUGADOR") }
    var posicion     by rememberSaveable { mutableStateOf(UserRoles.posiciones.first()) }
    var equipo       by rememberSaveable { mutableStateOf("Sin equipo") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var posicionExpanded by rememberSaveable { mutableStateOf(false) }
    var equipoExpanded   by rememberSaveable { mutableStateOf(false) }

    val tieneEquipo = UserRoles.rolesConEquipo.contains(selectedRole)

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Añadir Usuario", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            UserFormField("Nombre completo", nombre, { nombre = it })
            Spacer(modifier = Modifier.height(14.dp))

            UserFormField("Email", email, { email = it }, KeyboardType.Email)
            Spacer(modifier = Modifier.height(14.dp))

            UserFormField("Contraseña", password, { password = it }, isPassword = true)
            Spacer(modifier = Modifier.height(14.dp))

            UserFormField("Edad", edad, { edad = it.filter { c -> c.isDigit() } }, KeyboardType.Number)
            Spacer(modifier = Modifier.height(14.dp))

            UserFormField("Teléfono", telefono, { telefono = it }, KeyboardType.Phone)
            Spacer(modifier = Modifier.height(20.dp))

            // Rol
            Text("Rol", color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))
            UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedRole == roleKey,
                        onClick  = {
                            selectedRole = roleKey
                            if (!UserRoles.rolesConEquipo.contains(roleKey)) {
                                posicion = UserRoles.posiciones.first()
                                equipo   = "Sin equipo"
                            }
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = BlueLight)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(roleLabel, color = White, fontSize = 15.sp)
                }
            }

            // Campos de jugador/entrenador/árbitro
            if (tieneEquipo) {
                Spacer(modifier = Modifier.height(20.dp))

                // Posición (solo JUGADOR)
                if (selectedRole == "JUGADOR") {
                    Text("Posición", color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded          = posicionExpanded,
                        onExpandedChange  = { posicionExpanded = it },
                        modifier          = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value        = posicion,
                            onValueChange = {},
                            readOnly     = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posicionExpanded) },
                            colors       = darkFieldColors(),
                            shape        = RoundedCornerShape(8.dp),
                            modifier     = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded          = posicionExpanded,
                            onDismissRequest  = { posicionExpanded = false }
                        ) {
                            UserRoles.posiciones.forEach { pos ->
                                DropdownMenuItem(
                                    text    = { Text(pos) },
                                    onClick = { posicion = pos; posicionExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Equipo (de BD)
                Text("Equipo", color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                val teamOptions = listOf("Sin equipo") + teams.map { it.nombre }
                ExposedDropdownMenuBox(
                    expanded          = equipoExpanded,
                    onExpandedChange  = { equipoExpanded = it },
                    modifier          = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value         = equipo,
                        onValueChange = {},
                        readOnly      = true,
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = equipoExpanded) },
                        colors        = darkFieldColors(),
                        shape         = RoundedCornerShape(8.dp),
                        modifier      = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded         = equipoExpanded,
                        onDismissRequest = { equipoExpanded = false }
                    ) {
                        teamOptions.forEach { eq ->
                            DropdownMenuItem(
                                text    = { Text(eq) },
                                onClick = { equipo = eq; equipoExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            errorMessage?.let {
                Text(it, color = Color(0xFFEF5350), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() ->
                            errorMessage = "Nombre, email y contraseña son obligatorios"
                        edad.isBlank() ->
                            errorMessage = "La edad es obligatoria"
                        edad.toIntOrNull() == null || edad.toInt() < 1 || edad.toInt() > 120 ->
                            errorMessage = "Introduce una edad válida (1–120)"
                        telefono.isBlank() ->
                            errorMessage = "El teléfono es obligatorio"
                        else -> {
                            errorMessage = null
                            viewModel.addUser(
                                User(
                                    id       = 0,
                                    nombre   = nombre,
                                    email    = email,
                                    password = password,
                                    rol      = selectedRole,
                                    edad     = edad.toInt(),
                                    telefono = telefono,
                                    posicion = if (selectedRole == "JUGADOR") posicion else null,
                                    equipo   = if (tieneEquipo && equipo != "Sin equipo") equipo else null
                                )
                            )
                            navController.popBackStack()
                        }
                    }
                },
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape    = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text("Guardar Usuario", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Helpers compartidos ───────────────────────────────────────────────────────

@Composable
fun UserFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Text(label, color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value                  = value,
        onValueChange          = onValueChange,
        singleLine             = true,
        visualTransformation   = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions        = KeyboardOptions(keyboardType = keyboardType),
        colors                 = darkFieldColors(),
        shape                  = RoundedCornerShape(8.dp),
        modifier               = Modifier.fillMaxWidth()
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun darkFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor   = CardColor,
    unfocusedContainerColor = CardColor,
    focusedBorderColor      = BlueLight,
    unfocusedBorderColor    = TextSecondary,
    focusedTextColor        = White,
    unfocusedTextColor      = White,
    cursorColor             = BlueLight
)