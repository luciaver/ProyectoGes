package com.example.gessport.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gessport.R
import com.example.gessport.data.RoomUserRepository
import com.example.gessport.database.AppDatabase
import com.example.gessport.domain.LogicLogin
import kotlinx.coroutines.launch
import Routes

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


// Inicializar Room y Repository
    val database = remember { AppDatabase.getDatabase(context) }
    val userRepository = remember { RoomUserRepository(database.userDao()) }
    val logic = remember { LogicLogin(userRepository) }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var rememberPassword by rememberSaveable { mutableStateOf(false) }

    val primaryDark = Color(0xFF1E1E1E)
    val secondaryDark = Color(0xFF2C2C2C)
    val purpleBorder = Color(0xFF8A2BE2)
    val purpleGradientStart = Color(0xFF8A2BE2)
    val blueGradientEnd = Color(0xFF4682B4)

    val glowingBorderBrush = remember {
        Brush.horizontalGradient(
            colors = listOf(
                purpleGradientStart.copy(alpha = 0.8f),
                purpleBorder,
                Color.White.copy(alpha = 0.5f)
            )
        )
    }

    Surface(
        color = primaryDark,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Imagen de cabecera
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.foto_padel),
                    contentDescription = "Fondo de Padel",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = "GesSport",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Campo Email
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Email", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Introduce tu email", color = Color(0xFFAAAAAA)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = secondaryDark,
                        unfocusedContainerColor = secondaryDark,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, glowingBorderBrush, RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Contraseña
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Contraseña", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Introduce tu contraseña", color = Color(0xFFAAAAAA)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = secondaryDark,
                        unfocusedContainerColor = secondaryDark,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, glowingBorderBrush, RoundedCornerShape(8.dp))
                )
            }

            // Checkbox para recordar contraseña
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberPassword,
                    onCheckedChange = { rememberPassword = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = purpleGradientStart,
                        uncheckedColor = Color.White,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "Recordar contraseña",
                    color = Color.White,
                    modifier = Modifier.clickable { rememberPassword = !rememberPassword }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Boton de la loginca
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val user = logic.comprobarLogin(email, password)
                            errorMessage = null

                            // Verificar si es admin y si es pues te lleva al panel del admin
                            if (user.rol.equals("ADMIN_DEPORTIVO", ignoreCase = true)) {
                                navController.navigate(Routes.AdminPanel)
                            } else {
                                navController.navigate("${Routes.Home}/${user.nombre}")
                            }
                        } catch (e: IllegalArgumentException) {
                            errorMessage = e.message
                        }
                    }
                },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(purpleGradientStart, blueGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Inicia sesión",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Mensaje de error
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Enlace de registro
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "¿No tienes cuenta?", color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Regístrate",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Iconos sociales
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialMediaIcon(R.drawable.cromo)
                SocialMediaIcon(R.drawable.instagram)
                SocialMediaIcon(R.drawable.facebook)
            }
        }
    }
}

@Composable
fun SocialMediaIcon(resourceId: Int) {
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = null,
        modifier = Modifier
            .size(30.dp)
            .clickable { }
    )
}