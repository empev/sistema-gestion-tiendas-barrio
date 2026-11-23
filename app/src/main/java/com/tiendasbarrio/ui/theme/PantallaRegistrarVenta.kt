
package com.tiendasbarrio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tiendasbarrio.data.entity.AbonoEntity
import com.tiendasbarrio.data.entity.DetalleVentaEntity
import com.tiendasbarrio.data.entity.ProductoEntity
import com.tiendasbarrio.data.entity.VentaEntity
import com.tiendasbarrio.viewmodel.ClienteViewModel
import com.tiendasbarrio.viewmodel.ProductoViewModel
import com.tiendasbarrio.viewmodel.VentaCompletaViewModel
import java.text.NumberFormat
import java.util.Locale

private fun formatoPesos(valor: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("es", "CO")
    ).format(valor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarVenta(
    establecimientoId: Int,
    productoViewModel: ProductoViewModel,
    clienteViewModel: ClienteViewModel,
    ventaCompletaViewModel: VentaCompletaViewModel,
    regresar: () -> Unit
) {
    val productos by productoViewModel.productos.collectAsState()
    val clientes by clienteViewModel.clientes.collectAsState()
    val mensaje by ventaCompletaViewModel.mensaje.collectAsState()
    val procesando by ventaCompletaViewModel.procesando.collectAsState()
    val ventaRegistradaId by ventaCompletaViewModel.ventaRegistradaId.collectAsState()

    val cantidades = remember {
        mutableStateMapOf<Int, Int>()
    }

    var clienteSeleccionadoId by remember {
        mutableStateOf<Int?>(null)
    }

    var tipoVenta by remember {
        mutableStateOf("CONTADO")
    }

    var abonoTexto by remember {
        mutableStateOf("")
    }

    var observacion by remember {
        mutableStateOf("")
    }

    var productoExpandido by remember {
        mutableStateOf(false)
    }

    var productoSeleccionadoId by remember {
        mutableStateOf<Int?>(null)
    }

    var clienteExpandido by remember {
        mutableStateOf(false)
    }

    val total = productos.sumOf { producto ->
        val cantidad = cantidades[producto.id] ?: 0
        cantidad.toLong() * producto.precioVenta
    }

    val abonoInicial = abonoTexto.toLongOrNull() ?: 0L

    val productosSeleccionados = productos.filter {
        (cantidades[it.id] ?: 0) > 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Registrar venta")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Text(
                        text = "Productos disponibles",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = productoExpandido,
                        onExpandedChange = {
                            productoExpandido = !productoExpandido
                        }
                    ) {
                        OutlinedTextField(
                            value = productos.firstOrNull {
                                it.id == productoSeleccionadoId
                            }?.nombre ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text("Seleccionar producto")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = productoExpandido
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        DropdownMenu(
                            expanded = productoExpandido,
                            onDismissRequest = {
                                productoExpandido = false
                            }
                        ) {
                            productos.forEach { producto ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${producto.nombre} - " +
                                                    formatoPesos(producto.precioVenta) +
                                                    " | Stock: ${producto.existencia}"
                                        )
                                    },
                                    onClick = {
                                        productoSeleccionadoId = producto.id
                                        productoExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val producto = productos.firstOrNull {
                                it.id == productoSeleccionadoId
                            }

                            if (producto != null && producto.existencia > 0) {
                                val cantidadActual =
                                    cantidades[producto.id] ?: 0

                                if (cantidadActual < producto.existencia) {
                                    cantidades[producto.id] =
                                        cantidadActual + 1
                                }
                            }
                        },
                        enabled = productoSeleccionadoId != null
                    ) {
                        Text("Agregar producto")
                    }
                }

                item {
                    Text(
                        text = "Detalle de la venta",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (productosSeleccionados.isEmpty()) {
                    item {
                        Text("Todavía no has agregado productos.")
                    }
                }

                items(
                    items = productosSeleccionados,
                    key = { it.id }
                ) { producto ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                producto.nombre,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Precio: ${formatoPesos(producto.precioVenta)}"
                            )

                            Text(
                                "Existencias: ${producto.existencia}"
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        val actual =
                                            cantidades[producto.id] ?: 0

                                        if (actual > 1) {
                                            cantidades[producto.id] =
                                                actual - 1
                                        } else {
                                            cantidades.remove(producto.id)
                                        }
                                    }
                                ) {
                                    Text("-")
                                }

                                Text(
                                    text = "  ${cantidades[producto.id] ?: 0}  "
                                )

                                Button(
                                    onClick = {
                                        val actual =
                                            cantidades[producto.id] ?: 0

                                        if (actual < producto.existencia) {
                                            cantidades[producto.id] =
                                                actual + 1
                                        }
                                    },
                                    enabled = (cantidades[producto.id] ?: 0) <
                                            producto.existencia
                                ) {
                                    Text("+")
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    formatoPesos(
                                        producto.precioVenta *
                                                (cantidades[producto.id] ?: 0)
                                    ),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Tipo de venta",
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tipoVenta == "CONTADO",
                            onClick = {
                                tipoVenta = "CONTADO"
                                clienteSeleccionadoId = null
                                abonoTexto = total.toString()
                            }
                        )
                        Text("Contado")

                        RadioButton(
                            selected = tipoVenta == "CREDITO",
                            onClick = {
                                tipoVenta = "CREDITO"
                                abonoTexto = ""
                            }
                        )
                        Text("Crédito")
                    }
                }

                if (tipoVenta == "CREDITO") {
                    item {
                        Text(
                            "Cliente",
                            fontWeight = FontWeight.Bold
                        )

                        ExposedDropdownMenuBox(
                            expanded = clienteExpandido,
                            onExpandedChange = {
                                clienteExpandido = !clienteExpandido
                            }
                        ) {
                            OutlinedTextField(
                                value = clientes.firstOrNull {
                                    it.id == clienteSeleccionadoId
                                }?.nombre ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text("Seleccionar cliente")
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = clienteExpandido
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            DropdownMenu(
                                expanded = clienteExpandido,
                                onDismissRequest = {
                                    clienteExpandido = false
                                }
                            ) {
                                clientes.filter { it.activo }.forEach { cliente ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${cliente.nombre} - ${cliente.documento}"
                                            )
                                        },
                                        onClick = {
                                            clienteSeleccionadoId = cliente.id
                                            clienteExpandido = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Abono inicial",
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = abonoTexto,
                        onValueChange = {
                            if (it.all(Char::isDigit)) {
                                abonoTexto = it
                            }
                        },
                        label = {
                            Text("Valor abonado en pesos")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = {
                            observacion = it
                        },
                        label = {
                            Text("Observaciones (opcional)")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        "TOTAL: ${formatoPesos(total)}",
                        fontWeight = FontWeight.Bold
                    )

                    if (tipoVenta == "CREDITO") {
                        Text(
                            "Saldo pendiente: ${
                                formatoPesos(
                                    (total - abonoInicial).coerceAtLeast(0L)
                                )
                            }"
                        )
                    }
                }

                if (mensaje != null) {
                    item {
                        Text(
                            text = mensaje ?: "",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (ventaRegistradaId != null) {
                    item {
                        Text(
                            "Número de venta: $ventaRegistradaId",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val detalles = productosSeleccionados.map { producto ->
                        val cantidad = cantidades[producto.id] ?: 0

                        DetalleVentaEntity(
                            ventaId = 0,
                            productoId = producto.id,
                            nombreProducto = producto.nombre,
                            cantidad = cantidad,
                            precioUnitario = producto.precioVenta,
                            subtotal = cantidad.toLong() *
                                    producto.precioVenta
                        )
                    }

                    val abono = AbonoEntity(
                        ventaId = 0,
                        monto = abonoInicial,
                        observacion = "Abono inicial"
                    )

                    val venta = VentaEntity(
                        establecimientoId = establecimientoId,
                        clienteId = if (tipoVenta == "CREDITO") {
                            clienteSeleccionadoId
                        } else {
                            null
                        },
                        tipoVenta = tipoVenta,
                        total = total,
                        totalAbonado = abonoInicial,
                        observacion = observacion
                    )

                    ventaCompletaViewModel.registrarVentaCompleta(
                        venta = venta,
                        detalles = detalles,
                        abonoInicial = abono
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !procesando &&
                        productosSeleccionados.isNotEmpty() &&
                        total > 0 &&
                        abonoTexto.isNotBlank() &&
                        abonoInicial >= 0 &&
                        abonoInicial <= total &&
                        (
                                tipoVenta == "CONTADO" &&
                                        abonoInicial == total ||
                                        tipoVenta == "CREDITO" &&
                                        clienteSeleccionadoId != null
                                )
            ) {
                Text(
                    if (procesando) {
                        "Registrando..."
                    } else {
                        "Guardar venta"
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = regresar,
                modifier = Modifier.fillMaxWidth(),
                enabled = !procesando
            ) {
                Text("Regresar")
            }
        }
    }
}
