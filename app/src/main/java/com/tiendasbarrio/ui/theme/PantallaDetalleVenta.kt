
package com.tiendasbarrio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiendasbarrio.data.entity.AbonoEntity
import com.tiendasbarrio.viewmodel.DetalleVentaViewModel
import com.tiendasbarrio.viewmodel.AbonoViewModel
import com.tiendasbarrio.viewmodel.VentaViewModel
import com.tiendasbarrio.viewmodel.VentaCompletaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaDetalleVenta(
    ventaId: Int,
    ventaViewModel: VentaViewModel,
    detalleVentaViewModel: DetalleVentaViewModel,
    abonoViewModel: AbonoViewModel,
    ventaCompletaViewModel: VentaCompletaViewModel,
    regresar: () -> Unit
) {
    val ventas by ventaViewModel.ventas.collectAsState()
    val detalles by detalleVentaViewModel.detalles.collectAsState()
    val abonos by abonoViewModel.abonos.collectAsState()

    var montoAbono by remember { mutableStateOf("") }
    var observacionAbono by remember { mutableStateOf("") }

    val mensajeAbono by ventaCompletaViewModel.mensaje.collectAsState()
    val procesandoAbono by ventaCompletaViewModel.procesando.collectAsState()

    val venta = ventas.firstOrNull { it.id == ventaId }

    if (venta == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text("No se encontró la venta seleccionada.")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = regresar) {
                Text("Regresar")
            }
        }
        return
    }

    val formatoFecha = remember {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )
    }

    val fecha = formatoFecha.format(Date(venta.fechaVenta))
    val saldo = venta.total - venta.totalAbonado

    // ==========================================
    // ESTADO AUTOMÁTICO DE PAGO
    // ==========================================

    val estadoPago = when {
        venta.totalAbonado >= venta.total -> "PAGADA"
        venta.totalAbonado > 0 -> "ABONO PARCIAL"
        else -> "PENDIENTE DE PAGO"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Detalle de venta #${venta.id}",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // INFORMACIÓN GENERAL
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Información general",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Fecha: $fecha")
                Text("Tipo de venta: ${venta.tipoVenta}")
                Text("Total: ${venta.total} COP")
                Text("Total abonado: ${venta.totalAbonado} COP")
                Text("Saldo pendiente: ${saldo.coerceAtLeast(0)} COP")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Estado de pago: $estadoPago",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (estadoPago == "PAGADA") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // REGISTRAR ABONO
        // ==========================================

        if (venta.tipoVenta == "CREDITO" && saldo > 0) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Registrar abono",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = montoAbono,
                        onValueChange = { montoAbono = it },
                        label = {
                            Text("Monto del abono (COP)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = observacionAbono,
                        onValueChange = { observacionAbono = it },
                        label = {
                            Text("Observación (opcional)")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val monto = montoAbono.toLongOrNull()

                            if (
                                monto != null &&
                                monto > 0 &&
                                monto <= saldo
                            ) {
                                ventaCompletaViewModel.registrarAbono(
                                    ventaId = ventaId,
                                    abono = AbonoEntity(
                                        ventaId = ventaId,
                                        monto = monto,
                                        observacion = observacionAbono.trim()
                                    ),
                                    alCompletar = {
                                        abonoViewModel.cargarAbonos(ventaId)
                                        montoAbono = ""
                                        observacionAbono = ""
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !procesandoAbono &&
                                (montoAbono.toLongOrNull()?.let {
                                    it > 0 && it <= saldo
                                } == true)
                    ) {
                        Text(
                            if (procesandoAbono) {
                                "Registrando..."
                            } else {
                                "Guardar abono"
                            }
                        )
                    }

                    if (mensajeAbono != null) {
                        Text(
                            text = mensajeAbono!!,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ==========================================
        // PRODUCTOS VENDIDOS
        // ==========================================

        Text(
            text = "Productos vendidos",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                items = detalles,
                key = { it.id }
            ) { detalle ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            detalle.nombreProducto,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Cantidad: ${detalle.cantidad}")
                        Text("Precio unitario: ${detalle.precioUnitario} COP")
                        Text("Subtotal: ${detalle.subtotal} COP")
                    }
                }
            }

            // ==========================================
            // HISTORIAL DE ABONOS
            // ==========================================

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Historial de abonos",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (abonos.isEmpty()) {
                item {
                    Text("No hay abonos registrados.")
                }
            } else {
                items(
                    items = abonos,
                    key = { "abono_${it.id}" }
                ) { abono ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text("Abono: ${abono.monto} COP")

                            Text(
                                "Fecha: ${
                                    formatoFecha.format(
                                        Date(abono.fechaAbono)
                                    )
                                }"
                            )

                            if (abono.observacion.isNotBlank()) {
                                Text("Observación: ${abono.observacion}")
                            }
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
            Text("Regresar al historial")
        }
    }
}
