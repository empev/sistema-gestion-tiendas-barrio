
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.dao.ClienteDao
import com.tiendasbarrio.data.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

class ClienteRepository(
    private val clienteDao: ClienteDao
) {

    suspend fun insertar(cliente: ClienteEntity): Long =
        clienteDao.insertar(cliente)

    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<ClienteEntity>> =
        clienteDao.obtenerPorEstablecimiento(establecimientoId)

    suspend fun obtenerPorId(id: Int): ClienteEntity? =
        clienteDao.obtenerPorId(id)

    fun buscarClientes(
        establecimientoId: Int,
        busqueda: String
    ): Flow<List<ClienteEntity>> =
        clienteDao.buscarClientes(establecimientoId, busqueda)

    suspend fun actualizar(cliente: ClienteEntity) =
        clienteDao.actualizar(cliente)

    suspend fun eliminar(cliente: ClienteEntity) =
        clienteDao.eliminar(cliente)

    suspend fun contarClientes(
        establecimientoId: Int
    ): Int =
        clienteDao.contarClientes(establecimientoId)
}
