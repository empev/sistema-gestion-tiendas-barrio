package com.tiendasbarrio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiendasbarrio.viewmodel.ReporteViewModel
import com.tiendasbarrio.viewmodel.GastoViewModel

@Composable
fun PantallaReportes(
    establecimientoId: Int,
    reporteViewModel: ReporteViewModel,
    gastoViewModel: GastoViewModel,
    regresar: () -> Unit,
    abrirReporteGastos: () -> Unit
) {
    val reporte by reporteViewModel.reporte.collectAsState()
    val cargando by reporteViewModel.cargando.collectAsState()
    val mensaje by reporteViewModel.mensaje.collectAsState()
    val totalGastos by gastoViewModel.totalGastos.collectAsState()

    LaunchedEffect(establecimientoId) {
        reporteViewModel.cargarReporte(establecimientoId)
        gastoViewModel.cargarTotalGastos(establecimientoId)
    }

    fun formatoPesos(valor: Long): String {
        return valor.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed() + " COP"
    }
    val ingresosRecibidos =
        reporte.totalContado + reporte.totalAbonosRecibidos

    val balanceCaja = ingresosRecibidos - totalGastos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Reportes de ventas",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (mensaje != null) {
            Text(
                text = mensaje!!,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // ==========================================
        // RESUMEN GENERAL
        // ==========================================

        Text(
            text = "Resumen general",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Cantidad de ventas",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = reporte.cantidadVentas.toString(),
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Total vendido")

                Text(
                    text = formatoPesos(reporte.totalVendido),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // VENTAS DE CONTADO
        // ==========================================

        Text(
            text = "Ventas de contado",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Cantidad: ${reporte.cantidadContado}")

                Text(
                    text = "Total: ${formatoPesos(reporte.totalContado)}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // VENTAS A CRÉDITO
        // ==========================================

        Text(
            text = "Ventas a crédito",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Cantidad: ${reporte.cantidadCredito}")

                Text(
                    text = "Total: ${formatoPesos(reporte.totalCredito)}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Saldo pendiente por cobrar",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatoPesos(reporte.saldoPendiente),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // ABONOS RECIBIDOS
        // ==========================================

        Text(
            text = "Abonos recibidos",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Total de dinero recibido por abonos",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatoPesos(reporte.totalAbonosRecibidos),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // RESUMEN FINANCIERO DE CAJA
        // ==========================================

        Text(
            text = "Balance financiero de caja",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Ingresos recibidos",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatoPesos(ingresosRecibidos),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total de gastos",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatoPesos(totalGastos),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Balance de caja",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = formatoPesos(balanceCaja),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (balanceCaja >= 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // REPORTE DE GASTOS POR CATEGORÍA
        // ==========================================

        OutlinedButton(
            onClick = abrirReporteGastos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver gastos por categoría")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // ACTUALIZAR REPORTES
        // ==========================================

        OutlinedButton(
            onClick = {
                reporteViewModel.cargarReporte(establecimientoId)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Actualizar reportes")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = regresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar al inicio")
        }
    }
}