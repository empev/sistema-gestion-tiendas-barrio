
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.AbonoEntity
import com.tiendasbarrio.data.repository.AbonoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AbonoViewModel(
    private val repository: AbonoRepository
) : ViewModel() {

    private val _abonos =
        MutableStateFlow<List<AbonoEntity>>(emptyList())
    val abonos: StateFlow<List<AbonoEntity>> =
        _abonos.asStateFlow()

    private val _totalAbonado = MutableStateFlow(0L)
    val totalAbonado: StateFlow<Long> =
        _totalAbonado.asStateFlow()

    private val _cantidadAbonos = MutableStateFlow(0)
    val cantidadAbonos: StateFlow<Int> =
        _cantidadAbonos.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    fun cargarAbonos(ventaId: Int) {
        if (ventaId <= 0) {
            _mensaje.value = "La venta no es válida"
            return
        }

        viewModelScope.launch {
            repository.obtenerPorVenta(ventaId).collectLatest { lista ->
                _abonos.value = lista
                _totalAbonado.value = lista.sumOf { it.monto }
                _cantidadAbonos.value = lista.size
            }
        }
    }

    fun registrarAbono(abono: AbonoEntity) {
        viewModelScope.launch {
            try {
                validarAbono(abono)
                repository.insertar(abono)
                _mensaje.value = "Abono registrado correctamente"
            } catch (e: IllegalArgumentException) {
                _mensaje.value = e.message
            } catch (e: Exception) {
                _mensaje.value = "No se pudo registrar el abono"
            }
        }
    }

    private fun validarAbono(abono: AbonoEntity) {
        require(abono.ventaId > 0) {
            "La venta no es válida"
        }

        require(abono.monto > 0) {
            "El monto del abono debe ser mayor que cero"
        }

        require(abono.observacion.length <= 250) {
            "La observación no puede superar 250 caracteres"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
