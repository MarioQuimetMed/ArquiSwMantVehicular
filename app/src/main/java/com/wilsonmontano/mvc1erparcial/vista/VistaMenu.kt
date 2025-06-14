package com.wilsonmontano.mvc1erparcial.vista

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun VistaInicio(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menú Principal", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = { navController.navigate("vehiculos") }) {
            Text("Gestionar Vehículos")
        }

        Button(onClick = { navController.navigate("talleres") }) {
            Text("Gestionar Talleres")
        }
        Button(onClick = { navController.navigate("tipoMantenimiento") }) {
            Text("Gestionar Tipo de Mantenimiento")
        }

        Button(onClick = { navController.navigate("mantenimientos") }) {
            Text("Gestionar Mantenimientos")
        }

        Button(onClick = { navController.navigate("gastos") }) {
            Text("Gestionar Gastos")
        }


//        Button(onClick = { navController.navigate("exportar") }) {
//            Text("Exportar PDF")
//        }

    }
}
