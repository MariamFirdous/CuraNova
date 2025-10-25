package com.example.curanova.ui.theme.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ResultsScreen(navController: NavController, reportId: String) {
    Text(text = "Results Screen for report $reportId")
}