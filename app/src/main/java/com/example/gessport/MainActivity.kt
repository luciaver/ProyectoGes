package com.example.gessport

import Navigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gessport.ui.backend.ges_user.AdminPanelScreen
import com.example.gessport.ui.backend.ges_user.AddUserScreen
import com.example.gessport.ui.backend.ges_user.DeleteUserScreen
import com.example.gessport.ui.backend.ges_user.EditUserScreen
import com.example.gessport.ui.backend.ges_user.GesUserScreen
import com.example.gessport.ui.backend.ges_user.SelectUserScreen
import com.example.gessport.ui.home.HomeScreen
import com.example.gessport.ui.login.LoginScreen
import com.example.gessport.ui.theme.GesSportTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GesSportTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login,
        modifier = Modifier.fillMaxSize()
    ) {
        // Pantalla del Login
        composable(Routes.Login) {
            LoginScreen(navController = navController)
        }

        // Pantalla de Home pero solo para usuarios que no sean admin
        composable(
            route = "${Routes.Home}/{nombre}",
            arguments = listOf(
                navArgument("nombre") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            HomeScreen(
                navController = navController,
                nombre = backStackEntry.arguments?.getString("nombre")
            )
        }

        // Pantalla de Panel de Administración
        composable(Routes.AdminPanel) {
            AdminPanelScreen(navController = navController)
        }

        // Pantalla de Gestión de Usuarios
        composable(Routes.GesUser) {
            GesUserScreen(navController = navController)
        }

        // Pantalla de Añadir Usuario
        composable(Routes.AddUser) {
            AddUserScreen(navController = navController)
        }

        // Pantalla de Seleccionar Usuario para Modificar
        composable(Routes.SelectUser) {
            SelectUserScreen(navController = navController)
        }

        // Pantalla de Editar Usuario
        composable(
            route = "${Routes.EditUser}/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            EditUserScreen(navController = navController, userId = userId)
        }

        // Pantalla de Eliminar Usuario
        composable(Routes.DeleteUser) {
            DeleteUserScreen(navController = navController)
        }
    }
}*/