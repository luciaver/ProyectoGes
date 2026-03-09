package com.example.proyectoGes.ui.backend.ges_user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.proyectoGes.ui.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteUserScreen(navController: NavHostController) {
    val context   = LocalContext.current
    val viewModel: GesUserViewModel = viewModel(factory = GesUserViewModelFactory(context))

    val users by viewModel.users.collectAsState()
    var showDialog     by rememberSaveable { mutableStateOf(false) }
    var userIdToDelete by rememberSaveable { mutableStateOf<Int?>(null) }

    val userToDelete = users.find { it.id == userIdToDelete }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Eliminar Usuario", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text(
                "Selecciona el usuario a eliminar:",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            if (users.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios para eliminar", color = TextSecondary, fontSize = 15.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(users) { user ->
                        DeleteUserCard(
                            user          = user,
                            onDeleteClick = { userIdToDelete = user.id; showDialog = true }
                        )
                    }
                }
            }
        }

        if (showDialog && userToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title  = { Text("Confirmar eliminación") },
                text   = { Text("¿Eliminar a ${userToDelete.nombre}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            userIdToDelete?.let { viewModel.deleteUser(it) }
                            showDialog     = false
                            userIdToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; userIdToDelete = null }) {
                        Text("Cancelar")
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun DeleteUserCard(user: User, onDeleteClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nombre, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(user.email, color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text("Rol: ${getRoleName(user.rol)}", color = BlueLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Edad: ${user.edad}  |  Tel: ${user.telefono}", color = TextSecondary, fontSize = 12.sp)
                if (UserRoles.rolesConEquipo.contains(user.rol)) {
                    user.equipo?.let { Text("Equipo: $it", color = BlueLight, fontSize = 12.sp) }
                }
            }
            IconButton(
                onClick = onDeleteClick,
                colors  = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = White)
            }
        }
    }
}