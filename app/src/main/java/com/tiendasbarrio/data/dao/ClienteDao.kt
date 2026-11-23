
package com.tiendasbarrio.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tiendasbarrio.data.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(cliente: ClienteEntity): Long

    @Query("""
        SELECT * FROM clientes
        WHERE establecimientoId = :establecimientoId
        ORDER BY nombre ASC
    """)
    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<ClienteEntity>>

    @Query("""
        SELECT * FROM clientes
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): ClienteEntity?

    @Query("""
        SELECT * FROM clientes
        WHERE establecimientoId = :establecimientoId
        AND (
            nombre LIKE '%' || :busqueda || '%'
            OR documento LIKE '%' || :busqueda || '%'
        )
        ORDER BY nombre ASC
    """)
    fun buscarClientes(
        establecimientoId: Int,
        busqueda: String
    ): Flow<List<ClienteEntity>>

    @Update
    suspend fun actualizar(cliente: ClienteEntity)

    @Delete
    suspend fun eliminar(cliente: ClienteEntity)

    @Query("""
        SELECT COUNT(*) FROM clientes
        WHERE establecimientoId = :establecimientoId
    """)
    suspend fun contarClientes(
        establecimientoId: Int
    ): Int
}
