

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
import com.example.ProyectoGes.ui.home.BlueLight
import com.example.ProyectoGes.ui.home.BlueMain
import com.example.ProyectoGes.ui.home.CardColor
import com.example.ProyectoGes.ui.home.DarkBg
import com.example.ProyectoGes.ui.home.DarkSurface
import com.example.ProyectoGes.ui.home.TextSecondary
import com.example.ProyectoGes.ui.home.White
import androidx.compose.ui.graphics.vector.Group

data class DashboardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {

    val items = listOf(
        DashboardItem("Usuarios", "Gestionar usuarios", Icons.Default.Person, Color(0xFF1565C0), Routes.GesUser),
        DashboardItem("Equipos", "Gestionar equipos", Icons.Default.Group, Color(0xFF0288D1), Routes.GesTeam),
        DashboardItem("Reservas", "Franjas horarias", Icons.Default.DateRange, Color(0xFF00695C), Routes.GesReservation),
        DashboardItem("Instalaciones", "Pistas y campos", Icons.Default.Place, Color(0xFF6A1B9A), Routes.GesFacility)
    )

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel de Administración", color = White, fontWeight = FontWeight.Bold)
                        Text("GesSport", color = BlueLight, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Cabecera de bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlueMain),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null, tint = White, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Bienvenido, Admin", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Gestiona todo el centro deportivo", color = White.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Gestión", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // Grid 2x2
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    DashboardCard(item = item, onClick = { navController.navigate(item.route) })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cerrar sesión
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión", color = White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.color,
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.title, color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(item.subtitle, color = TextSecondary, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}