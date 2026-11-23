
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.dao.EstablecimientoDao
import com.tiendasbarrio.data.entity.EstablecimientoEntity
import kotlinx.coroutines.flow.Flow

class EstablecimientoRepository(
    private val establecimientoDao: EstablecimientoDao
) {

    suspend fun insertar(establecimiento: EstablecimientoEntity) {
        establecimientoDao.insertar(establecimiento)
    }

    fun obtenerTodos(): Flow<List<EstablecimientoEntity>> {
        return establecimientoDao.obtenerTodos()
    }

    suspend fun obtenerPrimero(): EstablecimientoEntity? {
        return establecimientoDao.obtenerPrimero()
    }

    suspend fun obtenerPorId(id: Int): EstablecimientoEntity? {
        return establecimientoDao.obtenerPorId(id)
    }

    suspend fun obtenerActivo(): EstablecimientoEntity? {
        return establecimientoDao.obtenerActivo()
    }

    suspend fun seleccionarComoActivo(id: Int) {
        establecimientoDao.seleccionarComoActivo(id)
    }

    suspend fun eliminar(establecimiento: EstablecimientoEntity) {
        establecimientoDao.eliminar(establecimiento)
    }
}
