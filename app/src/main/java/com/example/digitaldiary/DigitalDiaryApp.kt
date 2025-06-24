package com.example.digitaldiary

@Composable
fun DigitalDiaryApp() {
    val navController = rememberNavController()

    MaterialTheme {
        NavGraph(navController = navController)
    }
}