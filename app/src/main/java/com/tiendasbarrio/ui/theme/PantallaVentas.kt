
package com.tiendasbarrio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiendasbarrio.viewmodel.VentaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaVentas(
    regresar: () -> Unit,
    abrirRegistroVenta: () -> Unit,
    ventaViewModel: VentaViewModel,
    alSeleccionarVenta: (Int) -> Unit
) {
    val ventas by ventaViewModel.ventas.collectAsState()
    val totalVentas by ventaViewModel.totalVentas.collectAsState()
    val totalVendido by ventaViewModel.totalVendido.collectAsState()

    val formatoFecha = remember {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Ventas y comprobantes",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Resumen de ventas",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Cantidad de ventas: $totalVentas")

                Text(
                    text = "Total vendido: ${
                        totalVendido.toString()
                            .reversed()
                            .chunked(3)
                            .joinToString(".")
                            .reversed()
                    } COP"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = abrirRegistroVenta,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar nueva venta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Historial de ventas",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (ventas.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Todavía no hay ventas registradas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = ventas,
                    key = { it.id }
                ) { venta ->

                    val fecha = formatoFecha.format(
                        Date(venta.fechaVenta)
                    )

                    val saldo = venta.total - venta.totalAbonado

                    val estadoPago = when {
                        venta.totalAbonado >= venta.total ->
                            "PAGADA"

                        venta.totalAbonado > 0 ->
                            "ABONO PARCIAL"

                        else ->
                            "PENDIENTE"
                    }

                    val colorEstado = when (estadoPago) {
                        "PAGADA" ->
                            MaterialTheme.colorScheme.primary

                        "ABONO PARCIAL" ->
                            MaterialTheme.colorScheme.tertiary

                        else ->
                            MaterialTheme.colorScheme.error
                    }

                    Card(
                        onClick = {
                            alSeleccionarVenta(venta.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Venta #${venta.id}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Fecha: $fecha")
                            Text("Tipo: ${venta.tipoVenta}")
                            Text("Total: ${venta.total} COP")
                            Text("Abonado: ${venta.totalAbonado} COP")
                            Text(
                                "Saldo pendiente: ${saldo.coerceAtLeast(0)} COP"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Estado de pago: $estadoPago",
                                style = MaterialTheme.typography.titleSmall,
                                color = colorEstado
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Toca para ver el detalle",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = regresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar al inicio")
        }
    }
}
