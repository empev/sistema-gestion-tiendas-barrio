
package com.tiendasbarrio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiendasbarrio.data.entity.DetalleVentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleVentaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(detalle: DetalleVentaEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarTodos(
        detalles: List<DetalleVentaEntity>
    ): List<Long>

    @Query("""
        SELECT * FROM detalles_venta
        WHERE ventaId = :ventaId
        ORDER BY id ASC
    """)
    fun obtenerPorVenta(
        ventaId: Int
    ): Flow<List<DetalleVentaEntity>>

    @Query("""
        SELECT * FROM detalles_venta
        WHERE ventaId = :ventaId
        ORDER BY id ASC
    """)
    suspend fun obtenerPorVentaDirecto(
        ventaId: Int
    ): List<DetalleVentaEntity>

    @Query("""
        SELECT COALESCE(SUM(subtotal), 0)
        FROM detalles_venta
        WHERE ventaId = :ventaId
    """)
    suspend fun obtenerTotalVenta(ventaId: Int): Long

    @Query("""
        SELECT COUNT(*) FROM detalles_venta
        WHERE ventaId = :ventaId
    """)
    suspend fun contarDetalles(ventaId: Int): Int

    @Query("""
        DELETE FROM detalles_venta
        WHERE ventaId = :ventaId
    """)
    suspend fun eliminarPorVenta(ventaId: Int)
}
