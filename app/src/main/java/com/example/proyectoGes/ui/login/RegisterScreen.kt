package com.example.proyectoGes.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectoGes.models.User
import com.example.proyectoGes.models.UserRoles
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModel
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModelFactory
import com.example.proyectoGes.ui.backend.ges_user.GesUserViewModel
import com.example.proyectoGes.ui.backend.ges_user.GesUserViewModelFactory
import com.example.proyectoGes.ui.backend.ges_user.UserFormField
import com.example.proyectoGes.ui.backend.ges_user.darkFieldColors
import com.example.proyectoGes.ui.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: GesUserViewModel = viewModel(factory = GesUserViewModelFactory(context))
    val teamVM: TeamViewModel = viewModel(factory = TeamViewModelFactory(context))
    val teams by teamVM.teams.collectAsState()

    var nombre    by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var edad      by remember { mutableStateOf("") }
    var telefono  by remember { mutableStateOf("") }
    var posicion  by remember { mutableStateOf(UserRoles.posiciones.first()) }
    var equipo    by remember { mutableStateOf("Sin equipo") }
    var error     by remember { mutableStateOf<String?>(null) }
    var posExpanded by remember { mutableStateOf(false) }
    var eqExpanded  by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Registrarse", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BlueMain), shape = RoundedCornerShape(12.dp)) {
                Text("Registro solo disponible para Jugadores", modifier = Modifier.padding(12.dp), color = White, fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))

            UserFormField("Nombre", nombre, { nombre = it })
            Spacer(Modifier.height(12.dp))
            UserFormField("Email", email, { email = it }, KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            UserFormField("Contraseña", password, { password = it }, isPassword = true)
            Spacer(Modifier.height(12.dp))
            UserFormField("Edad", edad, { edad = it.filter { c -> c.isDigit() } }, KeyboardType.Number)
            Spacer(Modifier.height(12.dp))
            UserFormField("Teléfono", telefono, { telefono = it }, KeyboardType.Phone)
            Spacer(Modifier.height(16.dp))

            Text("Posición", color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(expanded = posExpanded, onExpandedChange = { posExpanded = it }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = posicion, onValueChange = {}, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(posExpanded) },
                    colors = darkFieldColors(), shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = posExpanded, onDismissRequest = { posExpanded = false }) {
                    UserRoles.posiciones.forEach { pos ->
                        DropdownMenuItem(text = { Text(pos) }, onClick = { posicion = pos; posExpanded = false })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Equipo", color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
            val teamOptions = listOf("Sin equipo") + teams.map { it.nombre }
            ExposedDropdownMenuBox(expanded = eqExpanded, onExpandedChange = { eqExpanded = it }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = equipo, onValueChange = {}, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(eqExpanded) },
                    colors = darkFieldColors(), shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = eqExpanded, onDismissRequest = { eqExpanded = false }) {
                    teamOptions.forEach { eq ->
                        DropdownMenuItem(text = { Text(eq) }, onClick = { equipo = eq; eqExpanded = false })
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            error?.let { Text(it, color = Color(0xFFEF5350), fontSize = 13.sp); Spacer(Modifier.height(8.dp)) }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() -> error = "Nombre, email y contraseña son obligatorios"
                        edad.isBlank() || edad.toIntOrNull() == null -> error = "Introduce una edad válida"
                        telefono.isBlank() -> error = "El teléfono es obligatorio"
                        else -> {
                            vm.addUser(User(
                                id = 0, nombre = nombre, email = email, password = password,
                                rol = "JUGADOR", edad = edad.toInt(), telefono = telefono,
                                posicion = posicion, equipo = equipo.takeIf { it != "Sin equipo" }
                            ))
                            navController.navigate(Routes.Login) { popUpTo(Routes.Login) { inclusive = true } }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) { Text("Registrarse", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }

            Spacer(Modifier.height(32.dp))
        }
    }
}