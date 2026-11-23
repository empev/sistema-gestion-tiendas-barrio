
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.GastoEntity
import com.tiendasbarrio.data.repository.GastoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.tiendasbarrio.data.entity.GastoCategoriaResumen

class GastoViewModel(
    private val gastoRepository: GastoRepository
) : ViewModel() {

    private val _gastos =
        MutableStateFlow<List<GastoEntity>>(emptyList())

    val gastos: StateFlow<List<GastoEntity>> =
        _gastos.asStateFlow()

    private val _totalGastos =
        MutableStateFlow(0L)

    val totalGastos: StateFlow<Long> =
        _totalGastos.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _resumenPorCategoria =
        MutableStateFlow<List<GastoCategoriaResumen>>(emptyList())

    val resumenPorCategoria: StateFlow<List<GastoCategoriaResumen>> =
        _resumenPorCategoria.asStateFlow()

    // Cargar gastos de un establecimiento
    fun cargarGastos(establecimientoId: Int) {
        if (establecimientoId <= 0) {
            _mensaje.value = "Establecimiento no válido."
            return
        }

        viewModelScope.launch {
            gastoRepository
                .obtenerPorEstablecimiento(establecimientoId)
                .collect { lista ->
                    _gastos.value = lista
                    cargarTotalGastos(establecimientoId)
                }
        }
    }

    // Cargar el total de gastos
    fun cargarTotalGastos(establecimientoId: Int) {
        viewModelScope.launch {
            try {
                _totalGastos.value =
                    gastoRepository.obtenerTotalGastos(
                        establecimientoId
                    )
            } catch (e: Exception) {
                _mensaje.value =
                    "Error al consultar los gastos: ${e.message}"
            }
        }
    }
    fun cargarResumenPorCategoria(establecimientoId: Int) {
        viewModelScope.launch {
            gastoRepository
                .obtenerResumenPorCategoria(establecimientoId)
                .collect { resumen ->
                    _resumenPorCategoria.value = resumen
                }
        }
    }

    // Registrar un gasto
    fun registrarGasto(gasto: GastoEntity) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                if (gasto.establecimientoId <= 0) {
                    _mensaje.value = "Establecimiento no válido."
                    return@launch
                }

                if (gasto.categoria.isBlank()) {
                    _mensaje.value = "Selecciona una categoría."
                    return@launch
                }

                if (gasto.descripcion.isBlank()) {
                    _mensaje.value = "Ingresa una descripción."
                    return@launch
                }

                if (gasto.monto <= 0) {
                    _mensaje.value =
                        "El monto debe ser mayor que cero."
                    return@launch
                }

                gastoRepository.insertar(gasto)

                _mensaje.value = "Gasto registrado correctamente."

                cargarTotalGastos(gasto.establecimientoId)

            } catch (e: Exception) {
                _mensaje.value =
                    "No fue posible registrar el gasto: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // Actualizar un gasto
    fun actualizarGasto(gasto: GastoEntity) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                if (gasto.monto <= 0 ||
                    gasto.descripcion.isBlank() ||
                    gasto.categoria.isBlank()
                ) {
                    _mensaje.value =
                        "Verifica los datos del gasto."
                    return@launch
                }

                gastoRepository.actualizar(gasto)

                _mensaje.value = "Gasto actualizado correctamente."

                cargarTotalGastos(gasto.establecimientoId)

            } catch (e: Exception) {
                _mensaje.value =
                    "No fue posible actualizar el gasto: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // Eliminar un gasto
    fun eliminarGasto(gasto: GastoEntity) {
        viewModelScope.launch {
            try {
                gastoRepository.eliminar(gasto)

                _mensaje.value = "Gasto eliminado correctamente."

                cargarTotalGastos(gasto.establecimientoId)

            } catch (e: Exception) {
                _mensaje.value =
                    "No fue posible eliminar el gasto: ${e.message}"
            }
        }
    }

    // Limpiar mensaje
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
