
package com.tiendasbarrio.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiendasbarrio.data.entity.EstablecimientoEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction

@Dao
interface EstablecimientoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(establecimiento: EstablecimientoEntity)

    @Query("SELECT * FROM establecimientos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<EstablecimientoEntity>>

    @Query("SELECT * FROM establecimientos LIMIT 1")
    suspend fun obtenerPrimero(): EstablecimientoEntity?

    @Query("SELECT * FROM establecimientos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): EstablecimientoEntity?

    @Query("SELECT * FROM establecimientos WHERE activo = 1 LIMIT 1")
    suspend fun obtenerActivo(): EstablecimientoEntity?

    @Query("UPDATE establecimientos SET activo = 0")
    suspend fun desactivarTodos()

    @Query("UPDATE establecimientos SET activo = 1 WHERE id = :id")
    suspend fun activarPorId(id: Int)

    @Transaction
    suspend fun seleccionarComoActivo(id: Int) {
        desactivarTodos()
        activarPorId(id)
    }
    @Delete
    suspend fun eliminar(establecimiento: EstablecimientoEntity)
}
