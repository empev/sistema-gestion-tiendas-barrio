
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.dao.GastoDao
import com.tiendasbarrio.data.entity.GastoEntity
import kotlinx.coroutines.flow.Flow
import com.tiendasbarrio.data.entity.GastoCategoriaResumen

class GastoRepository(
    private val gastoDao: GastoDao
) {

    // Registrar un gasto
    suspend fun insertar(gasto: GastoEntity): Long {
        return gastoDao.insertar(gasto)
    }

    // Obtener gastos de un establecimiento
    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<GastoEntity>> {
        return gastoDao.obtenerPorEstablecimiento(
            establecimientoId
        )
    }

    // Obtener un gasto por su identificador
    suspend fun obtenerPorId(id: Int): GastoEntity? {
        return gastoDao.obtenerPorId(id)
    }

    // Obtener el total de gastos
    suspend fun obtenerTotalGastos(establecimientoId: Int): Long {
        return gastoDao.obtenerTotalGastos(establecimientoId)
    }
    fun obtenerResumenPorCategoria(
        establecimientoId: Int
    ): Flow<List<GastoCategoriaResumen>> =
        gastoDao.obtenerResumenPorCategoria(establecimientoId)

    // Obtener gastos por categoría
    fun obtenerPorCategoria(
        establecimientoId: Int,
        categoria: String
    ): Flow<List<GastoEntity>> {
        return gastoDao.obtenerPorCategoria(
            establecimientoId,
            categoria
        )
    }

    // Actualizar un gasto
    suspend fun actualizar(gasto: GastoEntity): Int {
        return gastoDao.actualizar(gasto)
    }

    // Eliminar un gasto
    suspend fun eliminar(gasto: GastoEntity): Int {
        return gastoDao.eliminar(gasto)
    }
}
