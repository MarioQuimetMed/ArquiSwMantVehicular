package com.wilsonmontano.mvc1erparcial

import VistaFormularioMantenimiento
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
//import com.wilsonmontano.mvc1erparcial.modelo.Taller
//import com.wilsonmontano.mvc1erparcial.modelo.TipoMantenimiento
//import com.wilsonmontano.mvc1erparcial.modelo.Vehiculo
import com.wilsonmontano.mvc1erparcial.ui.helpui.*
import com.wilsonmontano.mvc1erparcial.ui.theme.MVC1ERPARCIALTheme
import com.wilsonmontano.mvc1erparcial.vista.*
import com.wilsonmontano.mvc1erparcial.worker.RecordatorioWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVC1ERPARCIALTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val irA = intent?.getStringExtra("irA")

                    // Lanzar el WorkManager para recordatorios cada 12h
                    LaunchedEffect(Unit) {
                        val workRequest = PeriodicWorkRequestBuilder<RecordatorioWorker>(
                            12, TimeUnit.HOURS
                        )
                            .addTag("verificacion_recordatorios")
                            .build()

                        WorkManager.getInstance(applicationContext)
                            .enqueueUniquePeriodicWork(
                                "verificacion_diaria_recordatorios",
                                ExistingPeriodicWorkPolicy.KEEP,
                                workRequest
                            )
                    }

                    var vehiculoAEditar by remember { mutableStateOf<VehiculoUI?>(null) }
                    var tallerAEditar by remember { mutableStateOf<TallerUI?>(null) }
                    var tipoMantenimientoAEditar by remember { mutableStateOf<TipoMantenimientoUI?>(null) }
                    var mantenimientoAEditar by remember { mutableStateOf<MantenimientoUI?>(null) }
                    var idMantenimientoDetalle by remember { mutableStateOf(-1) }
                    var idMantenimientoGasto by remember { mutableStateOf(-1) }

                    NavHost(navController = navController, startDestination = "inicio") {

                        composable("inicio") {
                            VistaInicio(navController)
                        }

                        composable("vehiculos") {
                            VistaVehiculo(navToFormulario = { vehiculo ->
                                vehiculoAEditar = vehiculo
                                navController.navigate("formVehiculo")
                            })
                        }

                        composable("formVehiculo") {
                            VistaFormularioVehiculo(
                                vehiculoExistente = vehiculoAEditar,
                                onVolver = {
                                    navController.popBackStack("vehiculos", false)
                                    vehiculoAEditar = null
                                }
                            )
                        }

                        composable("talleres") {
                            VistaTaller(navToFormulario = { taller ->
                                tallerAEditar = taller
                                navController.navigate("formTaller")
                            })
                        }

                        composable("formTaller") {
                            VistaFormularioTaller(
                                tallerExistente = tallerAEditar,
                                onVolver = {
                                    navController.popBackStack("talleres", false)
                                    tallerAEditar = null
                                }
                            )
                        }

                        composable("tipoMantenimiento") {
                            VistaTipoMantenimiento(navToFormulario = { tipo ->
                                tipoMantenimientoAEditar = tipo
                                navController.navigate("formTipoMantenimiento")
                            })
                        }

                        composable("formTipoMantenimiento") {
                            VistaFormularioTipoMantenimiento(
                                tipoExistente = tipoMantenimientoAEditar,
                                onVolver = {
                                    navController.popBackStack("tipoMantenimiento", false)
                                    tipoMantenimientoAEditar = null
                                }
                            )
                        }

                        composable("mantenimientos") {
                            VistaMantenimiento(
                                navToFormulario = { mantenimiento ->
                                    mantenimientoAEditar = mantenimiento
                                    navController.navigate("formMantenimiento")
                                },
                                navToDetalle = { mantenimiento ->
                                    idMantenimientoDetalle = mantenimiento.id
                                    navController.navigate("detalleMantenimiento")
                                }
                            )
                        }

                        composable("formMantenimiento") {
                            VistaFormularioMantenimiento(
                                mantenimientoExistente = mantenimientoAEditar,
                                onVolver = {
                                    navController.popBackStack("mantenimientos", false)
                                    mantenimientoAEditar = null
                                }
                            )
                        }

                        composable("detalleMantenimiento") {
                            VistaDetallesMantenimiento(
                                idMantenimiento = idMantenimientoDetalle,
                                onVolver = {
                                    navController.popBackStack("mantenimientos", false)
                                    idMantenimientoDetalle = -1
                                }
                            )
                        }

                        composable("gastos") {
                            VistaGasto(navToFormularioGasto = { id ->
                                idMantenimientoGasto = id
                                navController.navigate("formularioGasto")
                            })
                        }

                        composable("formularioGasto") {
                            VistaFormularioGasto(
                                idMantenimiento = idMantenimientoGasto,
                                onVolver = {
                                    navController.popBackStack("gastos", false)
                                    idMantenimientoGasto = -1
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
