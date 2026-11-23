package com.tiendasbarrio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiendasbarrio.viewmodel.GastoViewModel
import java.util.Locale

@Composable
fun PantallaReporteGastos(
    establecimientoId: Int,
    gastoViewModel: GastoViewModel,
    regresar: () -> Unit
) {
    val resumen by gastoViewModel.resumenPorCategoria.collectAsState()

    LaunchedEffect(establecimientoId) {
        gastoViewModel.cargarResumenPorCategoria(establecimientoId)
    }

    fun formatoPesos(valor: Long): String {
        return "$ " + String.format(
            Locale("es", "CO"),
            "%,d",
            valor
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Reporte de gastos por categoría",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = regresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (resumen.isEmpty()) {
            Text("Todavía no hay gastos registrados por categoría.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    resumen,
                    key = { it.categoria }
                ) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = item.categoria,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = formatoPesos(item.total),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}