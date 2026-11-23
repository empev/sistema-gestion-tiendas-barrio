
package com.tiendasbarrio.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tiendasbarrio.data.entity.GastoEntity
import kotlinx.coroutines.flow.Flow
import com.tiendasbarrio.data.entity.GastoCategoriaResumen

@Dao
interface GastoDao {

    // Registrar un gasto
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(gasto: GastoEntity): Long

    // Consultar todos los gastos de un establecimiento
    @Query("""
        SELECT * FROM gastos
        WHERE establecimientoId = :establecimientoId
        ORDER BY fechaGasto DESC
    """)
    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<GastoEntity>>

    // Consultar un gasto por su identificador
    @Query("""
        SELECT * FROM gastos
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): GastoEntity?

    // Obtener el total de gastos de un establecimiento
    @Query("""
        SELECT COALESCE(SUM(monto), 0)
        FROM gastos
        WHERE establecimientoId = :establecimientoId
    """)
    suspend fun obtenerTotalGastos( establecimientoId: Int): Long
    @Query("""
    SELECT categoria, COALESCE(SUM(monto), 0) AS total
    FROM gastos
    WHERE establecimientoId = :establecimientoId
    GROUP BY categoria
    ORDER BY total DESC
""")
    fun obtenerResumenPorCategoria(
        establecimientoId: Int
    ): Flow<List<GastoCategoriaResumen>>

    // Consultar gastos por categoría
    @Query("""
        SELECT * FROM gastos
        WHERE establecimientoId = :establecimientoId
        AND categoria = :categoria
        ORDER BY fechaGasto DESC
    """)
    fun obtenerPorCategoria(
        establecimientoId: Int,
        categoria: String
    ): Flow<List<GastoEntity>>

    // Actualizar un gasto
    @Update
    suspend fun actualizar(gasto: GastoEntity): Int

    // Eliminar un gasto
    @Delete
    suspend fun eliminar(gasto: GastoEntity): Int
}
