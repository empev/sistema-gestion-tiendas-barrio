
package com.tiendasbarrio.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tiendasbarrio.data.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Registrar un producto
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(producto: ProductoEntity): Long

    // Consultar todos los productos de un establecimiento
    @Query("""
        SELECT * FROM productos
        WHERE establecimientoId = :establecimientoId
        ORDER BY nombre ASC
    """)
    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<ProductoEntity>>

    // Consultar un producto por su identificador
    @Query("""
        SELECT * FROM productos
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): ProductoEntity?

    // Buscar productos por nombre o código
    @Query("""
        SELECT * FROM productos
        WHERE establecimientoId = :establecimientoId
        AND (
            nombre LIKE '%' || :busqueda || '%'
            OR codigo LIKE '%' || :busqueda || '%'
        )
        ORDER BY nombre ASC
    """)
    fun buscarProductos(
        establecimientoId: Int,
        busqueda: String
    ): Flow<List<ProductoEntity>>

    // Actualizar información de un producto
    @Update
    suspend fun actualizar(producto: ProductoEntity)

    @Query("""
    UPDATE productos
    SET existencia = existencia - :cantidad
    WHERE id = :productoId
    AND existencia >= :cantidad
""")
    suspend fun descontarExistencias(
        productoId: Int,
        cantidad: Int
    ): Int

    // Eliminar un producto
    @Delete
    suspend fun eliminar(producto: ProductoEntity)

    // Consultar productos con inventario bajo
    @Query("""
        SELECT * FROM productos
        WHERE establecimientoId = :establecimientoId
        AND existencia <= stockMinimo
        ORDER BY existencia ASC
    """)
    fun obtenerStockBajo(
        establecimientoId: Int
    ): Flow<List<ProductoEntity>>

    // Consultar cantidad total de productos registrados
    @Query("""
        SELECT COUNT(*) FROM productos
        WHERE establecimientoId = :establecimientoId
    """)
    suspend fun contarProductos(
        establecimientoId: Int
    ): Int
}
