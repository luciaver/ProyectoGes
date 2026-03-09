package com.example.proyectoGes.ui.backend.ges_user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SelectUserScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: GesUserViewModel = viewModel(factory = GesUserViewModelFactory(context))
    val users by viewModel.users.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Usuario", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Selecciona el usuario a modificar:", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 14.dp))
            if (users.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios", color = TextSecondary, fontSize = 15.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(users) { user ->
                        SelectUserCard(user = user, onEditClick = { navController.navigate("${Routes.EditUser}/${user.id}") })
                    }
                }
            }
        }
    }
}

@Composable
fun SelectUserCard(user: User, onEditClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onEditClick), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = CardColor), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nombre, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(user.email, color = TextSecondary, fontSize = 13.sp)
                Text("Rol: ${getRoleName(user.rol)}", color = BlueLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (UserRoles.rolesConEquipo.contains(user.rol)) {
                    user.equipo?.let { Text("Equipo: $it", color = BlueLight, fontSize = 12.sp) }
                }
            }
            IconButton(onClick = onEditClick, colors = IconButtonDefaults.iconButtonColors(containerColor = BlueMain)) {
                Icon(Icons.Default.Edit, null, tint = White)
            }
        }
    }
}