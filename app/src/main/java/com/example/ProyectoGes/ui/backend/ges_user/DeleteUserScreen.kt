package com.example.ProyectoGes.ui.backend.ges_user

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ProyectoGes.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteUserScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: GesUserViewModel = viewModel(
        factory = GesUserViewModelFactory(context)
    )

    val users by viewModel.users.collectAsState()
    var showDialog      by rememberSaveable { mutableStateOf(false) }
    var userIdToDelete  by rememberSaveable { mutableStateOf<Int?>(null) }

    val redPrimary     = Color(0xFFFF0000)
    val grayBackground = Color(0xFFE0E0E0)
    val whiteCard      = Color(0xFFFFFFFF)

    val userToDelete = users.find { it.id == userIdToDelete }

    Scaffold(
        containerColor = grayBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text("Eliminar Usuario", color = Color.White, fontWeight = FontWeight.Bold)
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
                .padding(16.dp)
        ) {
            Text(
                text = "Selecciona el usuario a eliminar:",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay usuarios para eliminar",
                        color = Color.Black.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(users) { user ->
                        DeleteUserCard(
                            user = user,
                            whiteCard = whiteCard,
                            redPrimary = redPrimary,
                            onDeleteClick = {
                                userIdToDelete = user.id
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Diálogo de confirmación
        if (showDialog && userToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar a ${userToDelete.nombre}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            userIdToDelete?.let { viewModel.deleteUser(it) }
                            showDialog = false
                            userIdToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = redPrimary)
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        userIdToDelete = null
                    }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun DeleteUserCard(
    user: User,
    whiteCard: Color,
    redPrimary: Color,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = whiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Nombre
                Text(text = user.nombre, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                // Email
                Text(text = user.email, color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))

                // Rol
                Text(
                    text = "Rol: ${getRoleName(user.rol)}",
                    color = redPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))

                // Edad
                Text(text = "Edad: ${user.edad} años", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))

                // Teléfono
                Text(text = "Teléfono: ${user.telefono}", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)

                // Posición y Equipo solo para JUGADOR
                if (user.rol == "JUGADOR") {
                    Spacer(modifier = Modifier.height(4.dp))
                    user.posicion?.let {
                        Text(text = "Posición: $it", color = redPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    user.equipo?.let {
                        Text(text = "Equipo: $it", color = redPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Botón eliminar
            IconButton(
                onClick = onDeleteClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = redPrimary)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
            }
        }
    }
}