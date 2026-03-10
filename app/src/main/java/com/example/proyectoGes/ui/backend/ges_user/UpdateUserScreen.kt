package com.example.proyectoGes.ui.backend.ges_user

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
import androidx.navigation.NavHostController
import com.example.proyectoGes.models.User
import com.example.proyectoGes.models.UserRoles
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModel
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModelFactory
import com.example.proyectoGes.ui.backend.ges_team.lightFieldColors
import com.example.proyectoGes.ui.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(navController: NavHostController, userId: Int) {
    val context   = LocalContext.current
    val viewModel: GesUserViewModel = viewModel(factory = GesUserViewModelFactory(context))
    val teamVM: TeamViewModel = viewModel(factory = TeamViewModelFactory(context))
    val teams by teamVM.teams.collectAsState()
    val users by viewModel.users.collectAsState()

    val userToEdit = remember(users) { users.find { it.id == userId } }

    var nombre       by remember { mutableStateOf("") }
    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var edad         by remember { mutableStateOf("") }
    var telefono     by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("JUGADOR") }
    var posicion     by remember { mutableStateOf(UserRoles.posiciones.first()) }
    var equipo       by remember { mutableStateOf("Sin equipo") }
    var initialized  by remember { mutableStateOf(false) }
    var error        by remember { mutableStateOf<String?>(null) }
    var posExpanded  by remember { mutableStateOf(false) }
    var eqExpanded   by remember { mutableStateOf(false) }

    LaunchedEffect(userToEdit) {
        if (userToEdit != null && !initialized) {
            nombre       = userToEdit.nombre
            email        = userToEdit.email
            password     = userToEdit.password
            edad         = userToEdit.edad.toString()
            telefono     = userToEdit.telefono
            selectedRole = userToEdit.rol
            posicion     = userToEdit.posicion ?: UserRoles.posiciones.first()
            equipo       = userToEdit.equipo ?: "Sin equipo"
            initialized  = true
        }
    }

    val tieneEquipo = UserRoles.rolesConEquipo.contains(selectedRole)

    if (userToEdit == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Modificar Usuario", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            UserFormField("Nombre completo", nombre, { nombre = it })
            Spacer(Modifier.height(14.dp))
            UserFormField("Email", email, { email = it }, KeyboardType.Email)
            Spacer(Modifier.height(14.dp))
            UserFormField("Contraseña", password, { password = it }, isPassword = true)
            Spacer(Modifier.height(14.dp))
            UserFormField("Edad", edad, { edad = it.filter { c -> c.isDigit() } }, KeyboardType.Number)
            Spacer(Modifier.height(14.dp))
            UserFormField("Teléfono", telefono, { telefono = it }, KeyboardType.Phone)
            Spacer(Modifier.height(20.dp))

            Text("Rol", color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
            UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedRole == roleKey, onClick = {
                        selectedRole = roleKey
                        if (!UserRoles.rolesConEquipo.contains(roleKey)) { posicion = UserRoles.posiciones.first(); equipo = "Sin equipo" }
                    }, colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue))
                    Spacer(Modifier.width(6.dp))
                    Text(roleLabel, color = TextPrimary, fontSize = 15.sp)
                }
            }

            if (tieneEquipo) {
                Spacer(Modifier.height(20.dp))
                if (selectedRole == "JUGADOR") {
                    Text("Posición", color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    ExposedDropdownMenuBox(expanded = posExpanded, onExpandedChange = { posExpanded = it }, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(value = posicion, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(posExpanded) }, colors = lightFieldColors(), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().menuAnchor())
                        ExposedDropdownMenu(expanded = posExpanded, onDismissRequest = { posExpanded = false }) {
                            UserRoles.posiciones.forEach { pos -> DropdownMenuItem(text = { Text(pos) }, onClick = { posicion = pos; posExpanded = false }) }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }
                Text("Equipo", color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                val teamOptions = listOf("Sin equipo") + teams.map { it.nombre }
                ExposedDropdownMenuBox(expanded = eqExpanded, onExpandedChange = { eqExpanded = it }, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = equipo, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(eqExpanded) }, colors = lightFieldColors(), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = eqExpanded, onDismissRequest = { eqExpanded = false }) {
                        teamOptions.forEach { eq -> DropdownMenuItem(text = { Text(eq) }, onClick = { equipo = eq; eqExpanded = false }) }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            error?.let { Text(it, color = DangerRed, fontSize = 13.sp); Spacer(Modifier.height(8.dp)) }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() -> error = "Nombre, email y contraseña son obligatorios"
                        edad.toIntOrNull() == null || edad.toInt() < 1 -> error = "Introduce una edad válida"
                        telefono.isBlank() -> error = "El teléfono es obligatorio"
                        else -> {
                            viewModel.updateUser(User(userId, nombre, email, password, selectedRole, edad.toInt(), telefono, if (selectedRole == "JUGADOR") posicion else null, if (tieneEquipo && equipo != "Sin equipo") equipo else null))
                            navController.popBackStack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(54.dp)
            ) { Text("Actualizar Usuario", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }

            Spacer(Modifier.height(32.dp))
        }
    }
}