package com.example.proyectoGes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.proyectoGes.database.AppDatabase
import com.example.proyectoGes.navigation.Navigation
import com.example.proyectoGes.ui.theme.GesSportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDatabase.getDatabase(applicationContext)

        enableEdgeToEdge()
        setContent {
            GesSportTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Navigation()
                }
            }
        }
    }
}