
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.ProductoEntity
import com.tiendasbarrio.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    // Lista de productos
    private val _productos =
        MutableStateFlow<List<ProductoEntity>>(emptyList())

    val productos: StateFlow<List<ProductoEntity>> =
        _productos.asStateFlow()

    // Productos con stock bajo
    private val _productosStockBajo =
        MutableStateFlow<List<ProductoEntity>>(emptyList())

    val productosStockBajo: StateFlow<List<ProductoEntity>> =
        _productosStockBajo.asStateFlow()

    // Total de productos
    private val _totalProductos = MutableStateFlow(0)

    val totalProductos: StateFlow<Int> =
        _totalProductos.asStateFlow()

    // Mensajes para la interfaz
    private val _mensaje = MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    // Identificador del establecimiento activo
    private var establecimientoIdActual: Int? = null

    // Configurar inventario del establecimiento activo
    fun cargarInventario(establecimientoId: Int) {

        if (establecimientoId <= 0) {
            _mensaje.value = "Establecimiento no válido"
            return
        }

        if (establecimientoIdActual == establecimientoId) {
            return
        }

        establecimientoIdActual = establecimientoId

        viewModelScope.launch {
            repository.obtenerPorEstablecimiento(establecimientoId)
                .collectLatest { lista ->
                    _productos.value = lista
                    _totalProductos.value = lista.size
                }
        }

        viewModelScope.launch {
            repository.obtenerStockBajo(establecimientoId)
                .collectLatest { lista ->
                    _productosStockBajo.value = lista
                }
        }
    }

    // Registrar producto
    fun registrarProducto(producto: ProductoEntity) {

        viewModelScope.launch {
            try {
                val establecimientoId = establecimientoIdActual

                if (establecimientoId == null ||
                    producto.establecimientoId != establecimientoId
                ) {
                    _mensaje.value =
                        "El establecimiento seleccionado no es válido"
                    return@launch
                }

                validarProducto(producto)

                repository.insertar(producto)

                _mensaje.value = "Producto registrado correctamente"

            } catch (e: IllegalArgumentException) {
                _mensaje.value = e.message
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo registrar el producto. Verifica si el código ya existe."
            }
        }
    }

    // Actualizar producto
    fun actualizarProducto(producto: ProductoEntity) {

        viewModelScope.launch {
            try {
                val establecimientoId = establecimientoIdActual

                if (establecimientoId == null ||
                    producto.establecimientoId != establecimientoId
                ) {
                    _mensaje.value =
                        "El establecimiento seleccionado no es válido"
                    return@launch
                }

                validarProducto(producto)

                val productoExistente =
                    repository.obtenerPorId(producto.id)

                if (productoExistente == null ||
                    productoExistente.establecimientoId != establecimientoId
                ) {
                    _mensaje.value = "Producto no encontrado"
                    return@launch
                }

                repository.actualizar(producto)

                _mensaje.value = "Producto actualizado correctamente"

            } catch (e: IllegalArgumentException) {
                _mensaje.value = e.message
            } catch (e: Exception) {
                _mensaje.value = "No se pudo actualizar el producto"
            }
        }
    }

    // Eliminar producto
    fun eliminarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            try {
                val establecimientoId = establecimientoIdActual

                if (establecimientoId == null ||
                    producto.establecimientoId != establecimientoId
                ) {
                    _mensaje.value = "El establecimiento seleccionado no es válido"
                    return@launch
                }

                val productoExistente = repository.obtenerPorId(producto.id)

                if (productoExistente == null) {
                    _mensaje.value = "Producto no encontrado"
                    return@launch
                }

                repository.eliminar(productoExistente)

                _mensaje.value = "Producto eliminado correctamente"

            } catch (e: Exception) {
                _mensaje.value = "No se pudo eliminar el producto"
            }
        }
    }

    // Buscar productos
    fun buscarProductos(busqueda: String) {

        val establecimientoId = establecimientoIdActual ?: return

        viewModelScope.launch {
            repository.buscarProductos(
                establecimientoId,
                busqueda.trim()
            ).collectLatest { lista ->
                _productos.value = lista
            }
        }
    }

    // Restaurar listado completo
    fun restaurarInventario() {
        val establecimientoId = establecimientoIdActual ?: return

        viewModelScope.launch {
            repository.obtenerPorEstablecimiento(establecimientoId)
                .collectLatest { lista ->
                    _productos.value = lista
                }
        }
    }

    // Validar información del producto
    private fun validarProducto(producto: ProductoEntity) {

        require(producto.codigo.isNotBlank()) {
            "El código del producto es obligatorio"
        }

        require(producto.nombre.isNotBlank()) {
            "El nombre del producto es obligatorio"
        }

        require(producto.categoria.isNotBlank()) {
            "La categoría es obligatoria"
        }

        require(producto.precioCompra >= 0) {
            "El precio de compra no puede ser negativo"
        }

        require(producto.precioVenta >= 0) {
            "El precio de venta no puede ser negativo"
        }

        require(producto.existencia >= 0) {
            "La existencia no puede ser negativa"
        }

        require(producto.stockMinimo >= 0) {
            "El stock mínimo no puede ser negativo"
        }
    }

    // Limpiar mensaje
    fun limpiarMensaje() {
        _mensaje.value = null
    }
    fun mostrarMensaje(mensaje: String) {
        _mensaje.value = mensaje
    }
}
