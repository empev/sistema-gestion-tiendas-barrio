
package com.tiendasbarrio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiendasbarrio.data.entity.GastoEntity
import com.tiendasbarrio.viewmodel.GastoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarGasto(
    establecimientoId: Int,
    gastoViewModel: GastoViewModel,
    regresar: () -> Unit
) {
    var categoria by remember { mutableStateOf("Servicios públicos") }
    var descripcion by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }
    var mostrarCategorias by remember { mutableStateOf(false) }

    val mensaje by gastoViewModel.mensaje.collectAsState()
    val cargando by gastoViewModel.cargando.collectAsState()

    val categorias = listOf(
        "Servicios públicos",
        "Arriendo",
        "Compra de mercancía",
        "Transporte",
        "Mantenimiento",
        "Salarios",
        "Impuestos",
        "Otros"
    )

    val monto = montoTexto.toLongOrNull()

    LaunchedEffect(mensaje) {
        if (mensaje == "Gasto registrado correctamente") {
            gastoViewModel.limpiarMensaje()
            regresar()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registrar gasto",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedButton(
            onClick = regresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }

        Text("Categoría del gasto")

        ExposedDropdownMenuBox(
            expanded = mostrarCategorias,
            onExpandedChange = {
                mostrarCategorias = !mostrarCategorias
            }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccionar categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = mostrarCategorias
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = mostrarCategorias,
                onDismissRequest = {
                    mostrarCategorias = false
                }
            ) {
                categorias.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            categoria = opcion
                            mostrarCategorias = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción del gasto") },
            placeholder = { Text("Ej. Pago de energía") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = montoTexto,
            onValueChange = { nuevoValor ->
                if (nuevoValor.all { it.isDigit() }) {
                    montoTexto = nuevoValor
                }
            },
            label = { Text("Valor del gasto (COP)") },
            placeholder = { Text("Ej. 150000") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (mensaje != null) {
            Text(
                text = mensaje!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (
                    descripcion.isNotBlank() &&
                    monto != null &&
                    monto > 0
                ) {
                    gastoViewModel.registrarGasto(
                        GastoEntity(
                            establecimientoId = establecimientoId,
                            categoria = categoria,
                            descripcion = descripcion.trim(),
                            monto = monto
                        )
                    )
                }
            },
            enabled = !cargando &&
                    descripcion.isNotBlank() &&
                    monto != null &&
                    monto > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Guardar gasto")
            }
        }
    }
}
