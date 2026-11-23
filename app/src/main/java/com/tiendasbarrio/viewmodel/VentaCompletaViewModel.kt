
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.VentaEntity
import com.tiendasbarrio.data.entity.DetalleVentaEntity
import com.tiendasbarrio.data.entity.AbonoEntity
import com.tiendasbarrio.data.repository.VentaCompletaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VentaCompletaViewModel(
    private val repository: VentaCompletaRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _ventaRegistradaId = MutableStateFlow<Long?>(null)
    val ventaRegistradaId: StateFlow<Long?> =
        _ventaRegistradaId.asStateFlow()

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    fun registrarVentaCompleta(
        venta: VentaEntity,
        detalles: List<DetalleVentaEntity>,
        abonoInicial: AbonoEntity?
    ) {
        if (_procesando.value) return

        viewModelScope.launch {
            _procesando.value = true
            _mensaje.value = null
            _ventaRegistradaId.value = null

            try {
                val ventaId = repository.registrarVentaCompleta(
                    venta = venta,
                    detalles = detalles,
                    abonoInicial = abonoInicial
                )

                _ventaRegistradaId.value = ventaId
                _mensaje.value = "Venta registrada correctamente"

            } catch (e: IllegalArgumentException) {
                _mensaje.value = e.message
                    ?: "Los datos de la venta no son válidos"

            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo registrar la venta. No se guardaron los cambios."
            } finally {
                _procesando.value = false
            }
        }
    }

    fun registrarAbono(
        ventaId: Int,
        abono: AbonoEntity,
        alCompletar: () -> Unit = {}
    ) {
        if (_procesando.value) return

        viewModelScope.launch {
            _procesando.value = true
            _mensaje.value = null

            try {
                repository.registrarAbonoCompleto(
                    ventaId = ventaId,
                    abono = abono
                )

                _mensaje.value = "Abono registrado correctamente"
                alCompletar()

            } catch (e: IllegalArgumentException) {
                _mensaje.value = e.message
                    ?: "Los datos del abono no son válidos"

            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo registrar el abono. No se guardaron los cambios."
            } finally {
                _procesando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun limpiarResultado() {
        _ventaRegistradaId.value = null
    }
}
