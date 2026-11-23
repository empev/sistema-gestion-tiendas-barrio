
package com.tiendasbarrio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiendasbarrio.data.entity.ClienteEntity

private val AzulEditarCliente = Color(0xFF173B67)
private val FondoEditarCliente = Color(0xFFF4F6F9)

@Composable
fun PantallaEditarCliente(
    cliente: ClienteEntity,
    regresar: () -> Unit,
    mensaje: String?,
    limpiarMensaje: () -> Unit,
    actualizar: (
        String,
        String,
        String,
        String,
        String
    ) -> Unit
) {
    var documento by remember(cliente.id) {
        mutableStateOf(cliente.documento)
    }

    var nombre by remember(cliente.id) {
        mutableStateOf(cliente.nombre)
    }

    var telefono by remember(cliente.id) {
        mutableStateOf(cliente.telefono)
    }

    var direccion by remember(cliente.id) {
        mutableStateOf(cliente.direccion)
    }

    var correo by remember(cliente.id) {
        mutableStateOf(cliente.correo)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoEditarCliente)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = regresar) {
            Text("← Regresar")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "EDITAR CLIENTE",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AzulEditarCliente
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = documento,
            onValueChange = { documento = it },
            label = { Text("Documento de identidad *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = {
                limpiarMensaje()

                if (
                    documento.isBlank() ||
                    nombre.isBlank() ||
                    telefono.isBlank()
                ) {
                    return@Button
                }

                actualizar(
                    documento.trim(),
                    nombre.trim(),
                    telefono.trim(),
                    direccion.trim(),
                    correo.trim()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Guardar cambios")
        }

        Spacer(modifier = Modifier.height(12.dp))

        mensaje?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    color = AzulEditarCliente
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "* Campos obligatorios",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}
