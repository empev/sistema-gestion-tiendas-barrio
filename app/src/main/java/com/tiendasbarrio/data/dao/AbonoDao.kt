
package com.tiendasbarrio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiendasbarrio.data.entity.AbonoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AbonoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(abono: AbonoEntity): Long

    @Query("""
        SELECT * FROM abonos
        WHERE ventaId = :ventaId
        ORDER BY fechaAbono DESC
    """)
    fun obtenerPorVenta(
        ventaId: Int
    ): Flow<List<AbonoEntity>>

    @Query("""
        SELECT * FROM abonos
        WHERE ventaId = :ventaId
        ORDER BY fechaAbono DESC
    """)
    suspend fun obtenerPorVentaDirecto(
        ventaId: Int
    ): List<AbonoEntity>

    @Query("""
        SELECT COALESCE(SUM(monto), 0)
        FROM abonos
        WHERE ventaId = :ventaId
    """)
    suspend fun obtenerTotalAbonado(ventaId: Int): Long

    @Query("""
    SELECT COUNT(*) FROM abonos
    WHERE ventaId = :ventaId
""")
    suspend fun contarAbonos(ventaId: Int): Int

    @Query("""
        SELECT * FROM abonos
        WHERE fechaAbono BETWEEN :inicio AND :fin
        ORDER BY fechaAbono DESC
    """)
    fun obtenerPorPeriodo(
        inicio: Long,
        fin: Long
    ): Flow<List<AbonoEntity>>

    @Query("""
    SELECT COALESCE(SUM(monto), 0)
    FROM abonos
    WHERE ventaId IN (
        SELECT id
        FROM ventas
        WHERE establecimientoId = :establecimientoId
    )
""")
    suspend fun obtenerTotalAbonos(
        establecimientoId: Int
    ): Long
}
