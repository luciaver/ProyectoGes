package com.example.proyectoGes.ui.backend.ges_user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proyectoGes.models.User
import com.example.proyectoGes.models.UserRoles
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesUserScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: GesUserViewModel = viewModel(factory = GesUserViewModelFactory(context))
    val users by viewModel.users.collectAsState()
    val selectedRole = viewModel.selectedRole

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(12.dp))
            Text("Filtrar por rol:", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedRole == null,
                        onClick = { viewModel.onRoleSelected(null) },
                        label = { Text("TODOS") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueMain,
                            selectedLabelColor = White
                        )
                    )
                }
                items(UserRoles.allRoles.toList()) { (key, label) ->
                    FilterChip(
                        selected = selectedRole == key,
                        onClick = { viewModel.onRoleSelected(if (selectedRole == key) null else key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueMain,
                            selectedLabelColor = White
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Routes.AddUser) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)
                ) { Text("Añadir", fontSize = 13.sp) }
                Button(
                    onClick = { navController.navigate(Routes.SelectUser) },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                    shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)
                ) { Text("Modificar", fontSize = 13.sp) }
                Button(
                    onClick = { navController.navigate(Routes.DeleteUser) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)
                ) { Text("Eliminar", fontSize = 13.sp) }
            }

            Spacer(Modifier.height(16.dp))

            if (users.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios", color = TextSecondary, fontSize = 15.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(users) { user -> UserListItem(user = user) }
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(user.nombre, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(user.email, color = TextSecondary, fontSize = 13.sp)
            Text("Rol: ${getRoleName(user.rol)}", color = BlueLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("Edad: ${user.edad}  |  Tel: ${user.telefono}", color = TextSecondary, fontSize = 12.sp)
            if (UserRoles.rolesConEquipo.contains(user.rol)) {
                user.posicion?.let { Text("Posición: $it", color = BlueLight, fontSize = 12.sp) }
                user.equipo?.let { Text("Equipo: $it", color = BlueLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

fun getRoleName(roleKey: String): String = when (roleKey.uppercase()) {
    "ADMIN_DEPORTIVO" -> "Administrador Deportivo"
    "ENTRENADOR"      -> "Entrenador"
    "JUGADOR"         -> "Jugador"
    "ARBITRO"         -> "Árbitro"
    else              -> roleKey
}