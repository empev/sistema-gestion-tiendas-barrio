package com.tiendasbarrio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiendasbarrio.data.entity.ClienteEntity
import com.tiendasbarrio.viewmodel.ClienteViewModel

private val AzulClientes = Color(0xFF173B67)
private val FondoClientes = Color(0xFFF4F6F9)

@Composable
fun PantallaClientes(
    clienteViewModel: ClienteViewModel,
    regresar: () -> Unit,
    abrirRegistro: () -> Unit,
    abrirEdicion: (ClienteEntity) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var clienteEliminar by remember {
        mutableStateOf<ClienteEntity?>(null)
    }

    val clientes by clienteViewModel.clientes.collectAsState()
    val totalClientes by clienteViewModel.totalClientes.collectAsState()

    LaunchedEffect(busqueda) {
        if (busqueda.isBlank()) {
            clienteViewModel.restaurarClientes()
        } else {
            clienteViewModel.buscarClientes(busqueda)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClientes)
            .padding(20.dp)
    ) {
        TextButton(onClick = regresar) {
            Text("← Regresar al menú")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "GESTIÓN DE CLIENTES",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AzulClientes
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar por nombre o documento") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Resumen de clientes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulClientes
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Total de clientes: $totalClientes",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = abrirRegistro,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("＋ Registrar nuevo cliente")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Listado de clientes",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = AzulClientes
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (clientes.isEmpty()) {
            Text(
                "Todavía no hay clientes registrados.",
                color = Color.Gray
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(clientes, key = { it.id }) { cliente ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                cliente.nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulClientes
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Documento: ${cliente.documento}")
                            Text("Teléfono: ${cliente.telefono}")
                            Text("Dirección: ${cliente.direccion}")
                            Text("Correo: ${cliente.correo}")

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    abrirEdicion(cliente)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Editar cliente")
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    clienteEliminar = cliente
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                )
                            ) {
                                Text("Eliminar cliente")
                            }
                        }
                    }
                }
            }
        }
    }
    if (clienteEliminar != null) {

        AlertDialog(
            onDismissRequest = {
                clienteEliminar = null
            },
            title = {
                Text("Confirmar eliminación")
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar al cliente ${clienteEliminar?.nombre}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clienteEliminar?.let { cliente ->
                            clienteViewModel.eliminarCliente(cliente)
                        }

                        clienteEliminar = null
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        clienteEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}