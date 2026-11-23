
package com.tiendasbarrio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tiendasbarrio.data.entity.VentaEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Update

@Dao
interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(venta: VentaEntity): Long

    @Query("""
        SELECT * FROM ventas
        WHERE establecimientoId = :establecimientoId
        ORDER BY fechaVenta DESC
    """)
    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<VentaEntity>>

    @Query("""
        SELECT * FROM ventas
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): VentaEntity?

    @Query("""
        SELECT * FROM ventas
        WHERE establecimientoId = :establecimientoId
        AND tipoVenta = :tipoVenta
        ORDER BY fechaVenta DESC
    """)
    fun obtenerPorTipo(
        establecimientoId: Int,
        tipoVenta: String
    ): Flow<List<VentaEntity>>

    @Query("""
        SELECT COUNT(*) FROM ventas
        WHERE establecimientoId = :establecimientoId
    """)
    suspend fun contarVentas(establecimientoId: Int): Int

    @Query("""
        SELECT COALESCE(SUM(total), 0) FROM ventas
        WHERE establecimientoId = :establecimientoId
    """)
    suspend fun obtenerTotalVendido(establecimientoId: Int): Long

    @Query("""
        SELECT * FROM ventas
        WHERE establecimientoId = :establecimientoId
        AND fechaVenta BETWEEN :inicio AND :fin
        ORDER BY fechaVenta DESC
    """)
    fun obtenerPorPeriodo(
        establecimientoId: Int,
        inicio: Long,
        fin: Long
    ): Flow<List<VentaEntity>>
    @Update
    suspend fun actualizar(venta: VentaEntity): Int


    // ==========================================
    // REPORTES DE VENTAS
    // ==========================================

    @Query("""
        SELECT COUNT(*) FROM ventas
        WHERE establecimientoId = :establecimientoId
        AND tipoVenta = :tipoVenta
    """)
    suspend fun contarVentasPorTipo(
        establecimientoId: Int,
        tipoVenta: String
    ): Int

    @Query("""
        SELECT COALESCE(SUM(total), 0) FROM ventas
        WHERE establecimientoId = :establecimientoId
        AND tipoVenta = :tipoVenta
    """)
    suspend fun obtenerTotalPorTipo(
        establecimientoId: Int,
        tipoVenta: String
    ): Long

    @Query("""
        SELECT COALESCE(SUM(total - totalAbonado), 0)
        FROM ventas
        WHERE establecimientoId = :establecimientoId
        AND tipoVenta = 'CREDITO'
    """)
    suspend fun obtenerSaldoPendiente(
        establecimientoId: Int
    ): Long

}
