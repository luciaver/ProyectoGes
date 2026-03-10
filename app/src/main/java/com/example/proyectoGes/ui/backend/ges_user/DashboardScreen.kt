package com.example.proyectoGes.ui.backend.ges_user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.home.*

data class DashboardItem(val title: String, val subtitle: String, val icon: ImageVector, val color: Color, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val items = listOf(
        DashboardItem("Usuarios",      "Gestionar usuarios",  Icons.Default.Person,       Color(0xFF2D5BE3), Routes.GesUser),
        DashboardItem("Equipos",       "Equipos y miembros",  Icons.Default.Group,        Color(0xFF0EA5E9), Routes.GesTeam),
        DashboardItem("Reservas",      "Control de reservas", Icons.Default.DateRange,    Color(0xFF059669), Routes.GesReservation),
        DashboardItem("Instalaciones", "Pistas y campos",     Icons.Default.Place,        Color(0xFF7C3AED), Routes.GesFacility),
        DashboardItem("Partidos",      "Gestionar partidos",  Icons.Default.SportsSoccer, Color(0xFFDC2626), Routes.GesMatch)
    )

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel de Administración", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("GesSport", color = PrimaryBlue, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PrimaryBlue), shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dashboard, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Bienvenido, Admin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Gestiona todo el centro deportivo", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Gestión", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    Card(
                        onClick = { navController.navigate(item.route) },
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(item.icon, item.title, tint = item.color, modifier = Modifier.size(42.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(item.subtitle, color = TextMuted, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}