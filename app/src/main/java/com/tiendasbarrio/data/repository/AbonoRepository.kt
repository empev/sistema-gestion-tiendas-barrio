
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.dao.AbonoDao
import com.tiendasbarrio.data.entity.AbonoEntity
import kotlinx.coroutines.flow.Flow

class AbonoRepository(
    private val abonoDao: AbonoDao
) {

    suspend fun insertar(abono: AbonoEntity): Long {
        return abonoDao.insertar(abono)
    }

    fun obtenerPorVenta(
        ventaId: Int
    ): Flow<List<AbonoEntity>> {
        return abonoDao.obtenerPorVenta(ventaId)
    }

    suspend fun obtenerPorVentaDirecto(
        ventaId: Int
    ): List<AbonoEntity> {
        return abonoDao.obtenerPorVentaDirecto(ventaId)
    }

    suspend fun obtenerTotalAbonado(ventaId: Int): Long {
        return abonoDao.obtenerTotalAbonado(ventaId)
    }

    suspend fun contarAbonos(ventaId: Int): Int {
        return abonoDao.contarAbonos(ventaId)
    }

    fun obtenerPorPeriodo(
        inicio: Long,
        fin: Long
    ): Flow<List<AbonoEntity>> {
        return abonoDao.obtenerPorPeriodo(inicio, fin)
    }

    suspend fun obtenerTotalAbonos(
        establecimientoId: Int
    ): Long {
        return abonoDao.obtenerTotalAbonos(establecimientoId)
    }
}
