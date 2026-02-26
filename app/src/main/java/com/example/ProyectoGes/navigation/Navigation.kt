import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ProyectoGes.ui.backend.ges_facility.AddFacilityScreen
import com.example.ProyectoGes.ui.backend.ges_facility.EditFacilityScreen
import com.example.ProyectoGes.ui.backend.ges_facility.GesFacilityScreen
import com.example.ProyectoGes.ui.backend.ges_reservation.AddReservationScreen
import com.example.ProyectoGes.ui.backend.ges_reservation.GesReservationScreen
import com.example.ProyectoGes.ui.backend.ges_reservation.MyReservationsScreen
import com.example.ProyectoGes.ui.backend.ges_team.AddTeamScreen
import com.example.ProyectoGes.ui.backend.ges_team.EditTeamScreen
import com.example.ProyectoGes.ui.backend.ges_team.GesTeamScreen
import com.example.ProyectoGes.ui.backend.ges_user.AddUserScreen
import com.example.ProyectoGes.ui.backend.ges_user.DeleteUserScreen
import com.example.ProyectoGes.ui.backend.ges_user.EditUserScreen
import com.example.ProyectoGes.ui.backend.ges_user.GesUserScreen
import com.example.ProyectoGes.ui.backend.ges_user.SelectUserScreen
import com.example.ProyectoGes.ui.home.HomeScreen
import com.example.ProyectoGes.ui.login.LoginScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Login) {

        // ── Login ──────────────────────────────────────────────────────────
        composable(Routes.Login) {
            LoginScreen(navController = navController)
        }

        // ── Home (usuario normal) ──────────────────────────────────────────
        // Ruta: home/{nombre}/{rol}
        composable(
            route = "${Routes.Home}/{nombre}/{rol}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            HomeScreen(
                navController = navController,
                nombre = backStackEntry.arguments?.getString("nombre"),
                rol = backStackEntry.arguments?.getString("rol")
            )
        }

        // ── Dashboard Admin ────────────────────────────────────────────────
        composable(Routes.AdminPanel) {
            DashboardScreen(navController = navController)
        }

        // ── Gestión Usuarios ───────────────────────────────────────────────
        composable(Routes.GesUser) { GesUserScreen(navController = navController) }
        composable(Routes.AddUser) { AddUserScreen(navController = navController) }
        composable(Routes.SelectUser) { SelectUserScreen(navController = navController) }
        composable(Routes.DeleteUser) { DeleteUserScreen(navController = navController) }
        composable(
            route = "${Routes.EditUser}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            EditUserScreen(
                navController = navController,
                userId = backStackEntry.arguments?.getInt("userId") ?: 0
            )
        }

        // ── Gestión Equipos ────────────────────────────────────────────────
        composable(Routes.GesTeam) { GesTeamScreen(navController = navController) }
        composable(Routes.AddTeam) { AddTeamScreen(navController = navController) }
        composable(
            route = "${Routes.EditTeam}/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            EditTeamScreen(
                navController = navController,
                teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            )
        }

        // ── Gestión Reservas ───────────────────────────────────────────────
        composable(Routes.GesReservation) { GesReservationScreen(navController = navController) }
        composable(Routes.AddReservation) { AddReservationScreen(navController = navController) }
        composable(Routes.MyReservations) { MyReservationsScreen(navController = navController) }

        // ── Gestión Instalaciones ──────────────────────────────────────────
        composable(Routes.GesFacility) { GesFacilityScreen(navController = navController) }
        composable(Routes.AddFacility) { AddFacilityScreen(navController = navController) }
        composable(
            route = "${Routes.EditFacility}/{facilityId}",
            arguments = listOf(navArgument("facilityId") { type = NavType.IntType })
        ) { backStackEntry ->
            EditFacilityScreen(
                navController = navController,
                facilityId = backStackEntry.arguments?.getInt("facilityId") ?: 0
            )
        }
    }
}

object Routes {
    // Auth
    const val Login = "login"

    // Home
    const val Home = "home"

    // Admin
    const val AdminPanel = "dashboard"

    // Usuarios
    const val GesUser = "gesuser"
    const val AddUser = "adduser"
    const val SelectUser = "selectuser"
    const val EditUser = "edituser"
    const val DeleteUser = "deleteuser"

    // Equipos
    const val GesTeam = "gesteam"
    const val AddTeam = "addteam"
    const val EditTeam = "editteam"

    // Reservas
    const val GesReservation = "gesreservation"
    const val AddReservation = "addreservation"
    const val MyReservations = "myreservations"

    // Instalaciones
    const val GesFacility = "gesfacility"
    const val AddFacility = "addfacility"
    const val EditFacility = "editfacility"
}