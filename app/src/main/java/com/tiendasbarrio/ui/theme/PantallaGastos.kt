
package com.tiendasbarrio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiendasbarrio.data.entity.GastoEntity
import com.tiendasbarrio.viewmodel.GastoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaGastos(
    establecimientoId: Int,
    gastoViewModel: GastoViewModel,
    regresar: () -> Unit,
    abrirRegistroGasto: () -> Unit
) {
    val gastos by gastoViewModel.gastos.collectAsState()
    val totalGastos by gastoViewModel.totalGastos.collectAsState()

    LaunchedEffect(establecimientoId) {
        gastoViewModel.cargarGastos(establecimientoId)
        gastoViewModel.cargarTotalGastos(establecimientoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gastos y egresos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = regresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total de gastos registrados")
                Text(
                    text = "$ ${String.format(Locale("es", "CO"), "%,d", totalGastos)}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = abrirRegistroGasto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar nuevo gasto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Historial de gastos",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (gastos.isEmpty()) {
            Text("Todavía no hay gastos registrados.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gastos, key = { it.id }) { gasto ->
                    TarjetaGasto(
                        gasto = gasto,
                        eliminar = {
                            gastoViewModel.eliminarGasto(
                                gasto = gasto
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaGasto(
    gasto: GastoEntity,
    eliminar: () -> Unit
) {
    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO"))
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = gasto.categoria,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Descripción: ${gasto.descripcion}")

            Text(
                "Valor: $ ${
                    String.format(
                        Locale("es", "CO"),
                        "%,d",
                        gasto.monto
                    )
                }"
            )

            Text(
                "Fecha: ${
                    formatoFecha.format(Date(gasto.fechaGasto))
                }"
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { mostrarConfirmacion = true }
            ) {
                Text("Eliminar gasto")
            }
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacion = false
            },
            title = {
                Text("Eliminar gasto")
            },
            text = {
                Text("¿Estás seguro de eliminar este gasto?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        eliminar()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
