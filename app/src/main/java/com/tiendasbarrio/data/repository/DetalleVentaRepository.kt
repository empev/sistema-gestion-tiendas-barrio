
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.dao.DetalleVentaDao
import com.tiendasbarrio.data.entity.DetalleVentaEntity
import kotlinx.coroutines.flow.Flow

class DetalleVentaRepository(
    private val detalleVentaDao: DetalleVentaDao
) {

    suspend fun insertar(
        detalle: DetalleVentaEntity
    ): Long {
        return detalleVentaDao.insertar(detalle)
    }

    suspend fun insertarTodos(
        detalles: List<DetalleVentaEntity>
    ): List<Long> {
        return detalleVentaDao.insertarTodos(detalles)
    }

    fun obtenerPorVenta(
        ventaId: Int
    ): Flow<List<DetalleVentaEntity>> {
        return detalleVentaDao.obtenerPorVenta(ventaId)
    }

    suspend fun obtenerPorVentaDirecto(
        ventaId: Int
    ): List<DetalleVentaEntity> {
        return detalleVentaDao.obtenerPorVentaDirecto(ventaId)
    }

    suspend fun obtenerTotalVenta(
        ventaId: Int
    ): Long {
        return detalleVentaDao.obtenerTotalVenta(ventaId)
    }

    suspend fun contarDetalles(
        ventaId: Int
    ): Int {
        return detalleVentaDao.contarDetalles(ventaId)
    }

    suspend fun eliminarPorVenta(
        ventaId: Int
    ) {
        detalleVentaDao.eliminarPorVenta(ventaId)
    }
}
