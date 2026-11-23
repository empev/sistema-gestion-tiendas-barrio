
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.DetalleVentaEntity
import com.tiendasbarrio.data.repository.DetalleVentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetalleVentaViewModel(
    private val repository: DetalleVentaRepository
) : ViewModel() {

    private val _detalles =
        MutableStateFlow<List<DetalleVentaEntity>>(emptyList())

    val detalles: StateFlow<List<DetalleVentaEntity>> =
        _detalles.asStateFlow()

    private val _totalVenta = MutableStateFlow(0L)

    val totalVenta: StateFlow<Long> =
        _totalVenta.asStateFlow()

    private val _totalProductos = MutableStateFlow(0)

    val totalProductos: StateFlow<Int> =
        _totalProductos.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    fun cargarDetalles(ventaId: Int) {

        if (ventaId <= 0) {
            _mensaje.value = "Venta no válida"
            return
        }

        viewModelScope.launch {
            repository.obtenerPorVenta(ventaId)
                .collectLatest { lista ->

                    _detalles.value = lista

                    _totalProductos.value = lista.size

                    _totalVenta.value =
                        lista.sumOf { it.subtotal }
                }
        }
    }

    fun registrarDetalle(detalle: DetalleVentaEntity) {

        viewModelScope.launch {
            try {

                validarDetalle(detalle)

                repository.insertar(detalle)

                _mensaje.value =
                    "Producto agregado a la venta correctamente"

            } catch (e: IllegalArgumentException) {

                _mensaje.value = e.message

            } catch (e: Exception) {

                _mensaje.value =
                    "No se pudo agregar el producto a la venta"
            }
        }
    }

    fun registrarDetalles(
        detalles: List<DetalleVentaEntity>
    ) {

        viewModelScope.launch {
            try {

                require(detalles.isNotEmpty()) {
                    "Debes agregar al menos un producto"
                }

                detalles.forEach { validarDetalle(it) }

                repository.insertarTodos(detalles)

                _mensaje.value =
                    "Productos agregados correctamente"

            } catch (e: IllegalArgumentException) {

                _mensaje.value = e.message

            } catch (e: Exception) {

                _mensaje.value =
                    "No se pudieron registrar los productos"
            }
        }
    }

    private fun validarDetalle(detalle: DetalleVentaEntity) {

        require(detalle.ventaId > 0) {
            "La venta no es válida"
        }

        require(detalle.productoId > 0) {
            "El producto no es válido"
        }

        require(detalle.nombreProducto.isNotBlank()) {
            "El nombre del producto es obligatorio"
        }

        require(detalle.cantidad > 0) {
            "La cantidad debe ser mayor que cero"
        }

        require(detalle.precioUnitario > 0) {
            "El precio unitario debe ser mayor que cero"
        }

        require(
            detalle.subtotal ==
                    detalle.cantidad.toLong() * detalle.precioUnitario
        ) {
            "El subtotal no coincide con la cantidad y el precio"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
