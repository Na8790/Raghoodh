package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.AppDatabase
import com.example.data.TajrubahRepository
import com.example.ui.TajrubahApp
import com.example.ui.TajrubahViewModel
import com.example.ui.TajrubahViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Database & Repository safely with Android lifecycleScope
    val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
    val repository = TajrubahRepository(database)

    // Instantiate TajrubahViewModel via custom Factory
    val viewModelFactory = TajrubahViewModelFactory(application, repository)
    val viewModel = ViewModelProvider(this, viewModelFactory)[TajrubahViewModel::class.java]

    setContent {
      MyApplicationTheme {
        TajrubahApp(viewModel = viewModel)
      }
    }
  }
}
