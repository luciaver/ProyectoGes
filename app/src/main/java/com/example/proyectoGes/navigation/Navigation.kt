package com.example.proyectoGes.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectoGes.ui.backend.ges_facility.*
import com.example.proyectoGes.ui.backend.ges_match.*
import com.example.proyectoGes.ui.backend.ges_reservation.*
import com.example.proyectoGes.ui.backend.ges_team.*
import com.example.proyectoGes.ui.backend.ges_user.*
import com.example.proyectoGes.ui.home.HomeScreen
import com.example.proyectoGes.ui.login.LoginScreen
import com.example.proyectoGes.ui.login.RegisterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.Login) {

        composable(Routes.Login) { LoginScreen(nav) }

        composable(Routes.Register) { RegisterScreen(nav) }

        composable(
            route = "${Routes.Home}/{userId}/{nombre}/{rol}/{equipo}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rol")    { type = NavType.StringType },
                navArgument("equipo") { type = NavType.StringType; nullable = true }
            )
        ) { back ->
            HomeScreen(
                navController = nav,
                userId  = back.arguments?.getInt("userId") ?: 0,
                nombre  = back.arguments?.getString("nombre"),
                rol     = back.arguments?.getString("rol"),
                equipo  = back.arguments?.getString("equipo")?.takeIf { it != "null" }
            )
        }

        composable(Routes.AdminPanel) { DashboardScreen(nav) }

        composable(Routes.GesUser)    { GesUserScreen(nav) }
        composable(Routes.AddUser)    { AddUserScreen(nav) }
        composable(Routes.SelectUser) { SelectUserScreen(nav) }
        composable(Routes.DeleteUser) { DeleteUserScreen(nav) }
        composable(
            "${Routes.EditUser}/{userId}",
            listOf(navArgument("userId") { type = NavType.IntType })
        ) { back -> EditUserScreen(nav, back.arguments?.getInt("userId") ?: 0) }

        composable(Routes.GesTeam) { GesTeamScreen(nav) }
        composable(Routes.AddTeam) { AddTeamScreen(nav) }
        composable(
            "${Routes.EditTeam}/{teamId}",
            listOf(navArgument("teamId") { type = NavType.IntType })
        ) { back -> EditTeamScreen(nav, back.arguments?.getInt("teamId") ?: 0) }

        composable(Routes.GesReservation) { GesReservationScreen(nav) }
        composable(
            "${Routes.AddReservation}/{userId}/{userName}",
            listOf(
                navArgument("userId")   { type = NavType.IntType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { back ->
            AddReservationScreen(
                navController = nav,
                userId   = back.arguments?.getInt("userId") ?: 0,
                userName = back.arguments?.getString("userName") ?: ""
            )
        }
        composable(
            "${Routes.MyReservations}/{userId}",
            listOf(navArgument("userId") { type = NavType.IntType })
        ) { back -> MyReservationsScreen(nav, back.arguments?.getInt("userId") ?: 0) }

        composable(Routes.GesFacility) { GesFacilityScreen(nav) }
        composable(Routes.AddFacility) { AddFacilityScreen(nav) }
        composable(
            "${Routes.EditFacility}/{facilityId}",
            listOf(navArgument("facilityId") { type = NavType.IntType })
        ) { back -> EditFacilityScreen(nav, back.arguments?.getInt("facilityId") ?: 0) }

        composable(Routes.GesMatch) { GesMatchScreen(nav) }
        composable(Routes.AddMatch) { AddMatchScreen(nav) }
        composable(
            "${Routes.EditMatch}/{matchId}",
            listOf(navArgument("matchId") { type = NavType.IntType })
        ) { back -> EditMatchScreen(nav, back.arguments?.getInt("matchId") ?: 0) }
    }
}