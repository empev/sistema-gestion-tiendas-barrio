
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.dao.VentaDao
import com.tiendasbarrio.data.entity.VentaEntity
import kotlinx.coroutines.flow.Flow

class VentaRepository(
    private val ventaDao: VentaDao
) {

    suspend fun insertar(venta: VentaEntity): Long {
        return ventaDao.insertar(venta)
    }

    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<VentaEntity>> {
        return ventaDao.obtenerPorEstablecimiento(establecimientoId)
    }

    suspend fun obtenerPorId(id: Int): VentaEntity? {
        return ventaDao.obtenerPorId(id)
    }

    fun obtenerPorTipo(
        establecimientoId: Int,
        tipoVenta: String
    ): Flow<List<VentaEntity>> {
        return ventaDao.obtenerPorTipo(
            establecimientoId,
            tipoVenta
        )
    }

    suspend fun contarVentas(establecimientoId: Int): Int {
        return ventaDao.contarVentas(establecimientoId)
    }

    suspend fun obtenerTotalVendido(
        establecimientoId: Int
    ): Long {
        return ventaDao.obtenerTotalVendido(establecimientoId)
    }

    fun obtenerPorPeriodo(
        establecimientoId: Int,
        inicio: Long,
        fin: Long
    ): Flow<List<VentaEntity>> {
        return ventaDao.obtenerPorPeriodo(
            establecimientoId,
            inicio,
            fin
        )
    }

    // ==========================================
    // REPORTES DE VENTAS
    // ==========================================

    suspend fun contarVentasPorTipo(
        establecimientoId: Int,
        tipoVenta: String
    ): Int {
        return ventaDao.contarVentasPorTipo(
            establecimientoId,
            tipoVenta
        )
    }

    suspend fun obtenerTotalPorTipo(
        establecimientoId: Int,
        tipoVenta: String
    ): Long {
        return ventaDao.obtenerTotalPorTipo(
            establecimientoId,
            tipoVenta
        )
    }

    suspend fun obtenerSaldoPendiente(
        establecimientoId: Int
    ): Long {
        return ventaDao.obtenerSaldoPendiente(
            establecimientoId
        )
    }
}
