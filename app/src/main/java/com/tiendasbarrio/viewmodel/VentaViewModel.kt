
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.VentaEntity
import com.tiendasbarrio.data.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VentaViewModel(
    private val repository: VentaRepository
) : ViewModel() {

    private val _ventas =
        MutableStateFlow<List<VentaEntity>>(emptyList())

    val ventas: StateFlow<List<VentaEntity>> =
        _ventas.asStateFlow()

    private val _totalVentas = MutableStateFlow(0)

    val totalVentas: StateFlow<Int> =
        _totalVentas.asStateFlow()

    private val _totalVendido = MutableStateFlow(0L)

    val totalVendido: StateFlow<Long> =
        _totalVendido.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    private var establecimientoIdActual: Int? = null

    fun cargarVentas(establecimientoId: Int) {

        if (establecimientoId <= 0) {
            _mensaje.value = "Establecimiento no válido"
            return
        }

        if (establecimientoIdActual == establecimientoId) return

        establecimientoIdActual = establecimientoId

        viewModelScope.launch {
            repository.obtenerPorEstablecimiento(establecimientoId)
                .collectLatest { lista ->
                    _ventas.value = lista
                    _totalVentas.value = lista.size

                    _totalVendido.value =
                        repository.obtenerTotalVendido(establecimientoId)
                }
        }
    }

    fun registrarVenta(venta: VentaEntity) {

        viewModelScope.launch {
            try {

                val establecimientoId = establecimientoIdActual

                if (
                    establecimientoId == null ||
                    venta.establecimientoId != establecimientoId
                ) {
                    _mensaje.value =
                        "El establecimiento seleccionado no es válido"
                    return@launch
                }

                validarVenta(venta)

                repository.insertar(venta)

                _mensaje.value =
                    "Venta registrada correctamente"

            } catch (e: IllegalArgumentException) {

                _mensaje.value = e.message

            } catch (e: Exception) {

                _mensaje.value =
                    "No se pudo registrar la venta"
            }
        }
    }

    private fun validarVenta(venta: VentaEntity) {

        require(venta.tipoVenta == "CONTADO" ||
                venta.tipoVenta == "CREDITO") {
            "El tipo de venta no es válido"
        }

        require(venta.total > 0) {
            "El total de la venta debe ser mayor que cero"
        }

        require(venta.totalAbonado >= 0) {
            "El abono inicial no puede ser negativo"
        }

        require(venta.totalAbonado <= venta.total) {
            "El abono inicial no puede superar el total"
        }

        if (venta.tipoVenta == "CONTADO") {
            require(venta.totalAbonado == venta.total) {
                "Una venta de contado debe estar pagada completamente"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
