
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiendasbarrio.data.entity.ClienteEntity
import com.tiendasbarrio.data.repository.ClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClienteViewModel(
    private val repository: ClienteRepository
) : ViewModel() {

    private val _clientes =
        MutableStateFlow<List<ClienteEntity>>(emptyList())

    val clientes: StateFlow<List<ClienteEntity>> =
        _clientes.asStateFlow()

    private val _totalClientes =
        MutableStateFlow(0)

    val totalClientes: StateFlow<Int> =
        _totalClientes.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    private var establecimientoIdActual: Int? = null

    fun cargarClientes(establecimientoId: Int) {

        if (establecimientoId <= 0) {
            _mensaje.value = "Establecimiento no válido"
            return
        }

        if (establecimientoIdActual == establecimientoId) return

        establecimientoIdActual = establecimientoId

        viewModelScope.launch {
            repository.obtenerPorEstablecimiento(establecimientoId)
                .collectLatest { lista ->
                    _clientes.value = lista
                    _totalClientes.value = lista.size
                }
        }
    }

    fun registrarCliente(cliente: ClienteEntity) {

        viewModelScope.launch {
            try {
                val establecimientoId = establecimientoIdActual

                if (
                    establecimientoId == null ||
                    cliente.establecimientoId != establecimientoId
                ) {
                    _mensaje.value = "El establecimiento seleccionado no es válido"
                    return@launch
                }

                validarCliente(cliente)

                repository.insertar(cliente)

                _mensaje.value = "Cliente registrado correctamente"

            } catch (e: IllegalArgumentException) {
                _mensaje.value = e.message
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo registrar el cliente. Verifica si el documento ya existe."
            }
        }
    }

    fun actualizarCliente(cliente: ClienteEntity) {

        viewModelScope.launch {
            try {
                val establecimientoId = establecimientoIdActual

                if (
                    establecimientoId == null ||
                    cliente.establecimientoId != establecimientoId
                ) {
                    _mensaje.value = "El establecimiento seleccionado no es válido"
                    return@launch
                }

                validarCliente(cliente)

                val existente = repository.obtenerPorId(cliente.id)

                if (
                    existente == null ||
                    existente.establecimientoId != establecimientoId
                ) {
                    _mensaje.value = "Cliente no encontrado"
                    return@launch
                }

                repository.actualizar(cliente)

                _mensaje.value = "Cliente actualizado correctamente"

            } catch (e: IllegalArgumentException) {
                _mensaje.value = e.message
            } catch (e: Exception) {
                _mensaje.value = "No se pudo actualizar el cliente"
            }
        }
    }

    fun eliminarCliente(cliente: ClienteEntity) {

        viewModelScope.launch {
            try {
                val establecimientoId = establecimientoIdActual

                if (
                    establecimientoId == null ||
                    cliente.establecimientoId != establecimientoId
                ) {
                    _mensaje.value = "El establecimiento seleccionado no es válido"
                    return@launch
                }

                val existente = repository.obtenerPorId(cliente.id)

                if (
                    existente == null ||
                    existente.establecimientoId != establecimientoId
                ) {
                    _mensaje.value = "Cliente no encontrado"
                    return@launch
                }

                repository.eliminar(existente)

                _mensaje.value = "Cliente eliminado correctamente"

            } catch (e: Exception) {
                _mensaje.value = "No se pudo eliminar el cliente"
            }
        }
    }

    fun buscarClientes(busqueda: String) {

        val establecimientoId = establecimientoIdActual ?: return

        viewModelScope.launch {
            repository.buscarClientes(establecimientoId, busqueda)
                .collectLatest { lista ->
                    _clientes.value = lista
                }
        }
    }

    fun restaurarClientes() {
        val establecimientoId = establecimientoIdActual ?: return
        cargarClientes(establecimientoId)
    }

    private fun validarCliente(cliente: ClienteEntity) {

        require(cliente.documento.isNotBlank()) {
            "El documento es obligatorio"
        }

        require(cliente.nombre.isNotBlank()) {
            "El nombre del cliente es obligatorio"
        }

        require(cliente.telefono.isNotBlank()) {
            "El teléfono es obligatorio"
        }
    }

    fun mostrarMensaje(mensaje: String) {
        _mensaje.value = mensaje
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
