
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.repository.VentaRepository
import com.tiendasbarrio.data.repository.AbonoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReporteVentas(
    val cantidadVentas: Int = 0,
    val totalVendido: Long = 0L,
    val cantidadContado: Int = 0,
    val cantidadCredito: Int = 0,
    val totalContado: Long = 0L,
    val totalCredito: Long = 0L,
    val saldoPendiente: Long = 0L,
    val totalAbonosRecibidos: Long = 0L
)

class ReporteViewModel(
    private val ventaRepository: VentaRepository,
    private val abonoRepository: AbonoRepository
) : ViewModel() {

    private val _reporte = MutableStateFlow(ReporteVentas())
    val reporte: StateFlow<ReporteVentas> = _reporte.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cargarReporte(establecimientoId: Int) {

        if (establecimientoId <= 0) {
            _mensaje.value = "Establecimiento no válido."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val cantidadVentas =
                    ventaRepository.contarVentas(establecimientoId)

                val totalVendido =
                    ventaRepository.obtenerTotalVendido(establecimientoId)

                val cantidadContado =
                    ventaRepository.contarVentasPorTipo(
                        establecimientoId,
                        "CONTADO"
                    )

                val cantidadCredito =
                    ventaRepository.contarVentasPorTipo(
                        establecimientoId,
                        "CREDITO"
                    )

                val totalContado =
                    ventaRepository.obtenerTotalPorTipo(
                        establecimientoId,
                        "CONTADO"
                    )

                val totalCredito =
                    ventaRepository.obtenerTotalPorTipo(
                        establecimientoId,
                        "CREDITO"
                    )

                val saldoPendiente =
                    ventaRepository.obtenerSaldoPendiente(establecimientoId)

                val totalAbonosRecibidos =
                    abonoRepository.obtenerTotalAbonos(establecimientoId)

                _reporte.value = ReporteVentas(
                    cantidadVentas = cantidadVentas,
                    totalVendido = totalVendido,
                    cantidadContado = cantidadContado,
                    cantidadCredito = cantidadCredito,
                    totalContado = totalContado,
                    totalCredito = totalCredito,
                    saldoPendiente = saldoPendiente,
                    totalAbonosRecibidos = totalAbonosRecibidos
                )

            } catch (e: Exception) {
                _mensaje.value =
                    "No fue posible cargar los reportes: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
