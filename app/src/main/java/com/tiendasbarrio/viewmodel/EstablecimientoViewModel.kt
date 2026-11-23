
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.EstablecimientoEntity
import com.tiendasbarrio.data.repository.EstablecimientoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstablecimientoViewModel(
    private val repository: EstablecimientoRepository
) : ViewModel() {

    // Lista de establecimientos registrados
    val establecimientos = repository.obtenerTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Registrar un nuevo establecimiento
    fun registrarEstablecimiento(
        nombre: String,
        propietario: String,
        municipio: String,
        direccion: String,
        telefono: String,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val establecimiento = EstablecimientoEntity(
                    nombre = nombre,
                    propietario = propietario,
                    municipio = municipio,
                    direccion = direccion,
                    telefono = telefono
                )

                repository.insertar(establecimiento)
                onResultado(true)

            } catch (e: Exception) {
                onResultado(false)
            }
        }
    }

    // Consultar el establecimiento activo
    suspend fun obtenerEstablecimientoActivo(): EstablecimientoEntity? {
        return repository.obtenerActivo()
    }

    // Seleccionar un establecimiento como activo
    fun seleccionarEstablecimiento(
        id: Int,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.seleccionarComoActivo(id)
                onResultado(true)

            } catch (e: Exception) {
                onResultado(false)
            }
        }
    }
}
