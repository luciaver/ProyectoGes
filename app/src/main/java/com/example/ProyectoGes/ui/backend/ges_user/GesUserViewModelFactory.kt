package com.example.ProyectoGes.ui.backend.ges_user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GesUserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GesUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GesUserViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
