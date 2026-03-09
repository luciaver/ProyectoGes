package com.example.proyectoGes.ui.login

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectoGes.data.RoomUserRepository
import com.example.proyectoGes.database.AppDatabase
import com.example.proyectoGes.domain.LogicLogin
import com.example.proyectoGes.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    val db    = remember { AppDatabase.getDatabase(context) }
    val repo  = remember { RoomUserRepository(db.userDao()) }
    val logic = remember { LogicLogin(repo) }

    var email        by rememberSaveable { mutableStateOf("") }
    var password     by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val dark   = Color(0xFF1E1E1E)
    val dark2  = Color(0xFF2C2C2C)
    val purple = Color(0xFF8A2BE2)
    val blue   = Color(0xFF4682B4)
    val brush  = remember { Brush.horizontalGradient(listOf(purple.copy(0.8f), purple, Color.White.copy(0.5f))) }

    Surface(color = dark, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header con gradiente — sin dependencia de R
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF3A0070), purple, blue, dark))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Círculo decorativo
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚽", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "GesSport",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                Text("Email", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Introduce tu email", color = Color(0xFFAAAAAA)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = dark2, unfocusedContainerColor = dark2,
                        focusedBorderColor      = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        cursorColor             = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    shape    = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().border(2.dp, brush, RoundedCornerShape(8.dp))
                )
            }

            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                Text("Contraseña", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Introduce tu contraseña", color = Color(0xFFAAAAAA)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = dark2, unfocusedContainerColor = dark2,
                        focusedBorderColor      = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        cursorColor             = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    shape    = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().border(2.dp, brush, RoundedCornerShape(8.dp))
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val user = logic.comprobarLogin(email, password)
                            errorMessage = null
                            if (user.rol.equals("ADMIN_DEPORTIVO", ignoreCase = true)) {
                                navController.navigate(Routes.AdminPanel)
                            } else {
                                val nombreEnc = Uri.encode(user.nombre)
                                val equipoEnc = Uri.encode(user.equipo ?: "null")
                                navController.navigate("${Routes.Home}/${user.id}/$nombreEnc/${user.rol}/$equipoEnc")
                            }
                        } catch (e: IllegalArgumentException) {
                            errorMessage = e.message
                        }
                    }
                },
                contentPadding = PaddingValues(),
                colors   = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape    = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(purple, blue))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Inicia sesión", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(16.dp))
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 32.dp))
            }

            Spacer(Modifier.height(48.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta?", color = Color.White)
                Spacer(Modifier.width(4.dp))
                Text(
                    "Regístrate",
                    color = Color.White, fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { navController.navigate(Routes.Register) }
                )
            }
        }
    }
}