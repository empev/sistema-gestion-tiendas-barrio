
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

private val AzulRegistroCliente = Color(0xFF173B67)
private val FondoRegistroCliente = Color(0xFFF4F6F9)

@Composable
fun PantallaRegistrarCliente(
    regresar: () -> Unit,
    mensaje: String?,
    limpiarMensaje: () -> Unit,
    registrar: (
        String,
        String,
        String,
        String,
        String
    ) -> Unit
) {
    var documento by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoRegistroCliente)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = regresar) {
            Text("← Regresar")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "REGISTRAR CLIENTE",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AzulRegistroCliente
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

                registrar(
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
            Text("Guardar cliente")
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
                    color = AzulRegistroCliente
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
