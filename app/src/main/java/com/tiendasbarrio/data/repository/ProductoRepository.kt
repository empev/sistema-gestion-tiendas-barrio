
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.dao.ProductoDao
import com.tiendasbarrio.data.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

class ProductoRepository(
    private val productoDao: ProductoDao
) {

    // Registrar un producto
    suspend fun insertar(producto: ProductoEntity): Long {
        return productoDao.insertar(producto)
    }

    // Obtener productos de un establecimiento
    fun obtenerPorEstablecimiento(
        establecimientoId: Int
    ): Flow<List<ProductoEntity>> {
        return productoDao.obtenerPorEstablecimiento(
            establecimientoId
        )
    }

    // Buscar producto por su identificador
    suspend fun obtenerPorId(
        id: Int
    ): ProductoEntity? {
        return productoDao.obtenerPorId(id)
    }

    // Buscar productos por nombre o código
    fun buscarProductos(
        establecimientoId: Int,
        busqueda: String
    ): Flow<List<ProductoEntity>> {
        return productoDao.buscarProductos(
            establecimientoId,
            busqueda
        )
    }

    // Actualizar información de un producto
    suspend fun actualizar(
        producto: ProductoEntity
    ) {
        productoDao.actualizar(producto)
    }

    // Eliminar un producto
    suspend fun eliminar(
        producto: ProductoEntity
    ) {
        productoDao.eliminar(producto)
    }

    // Consultar productos con inventario bajo
    fun obtenerStockBajo(
        establecimientoId: Int
    ): Flow<List<ProductoEntity>> {
        return productoDao.obtenerStockBajo(
            establecimientoId
        )
    }

    // Contar productos registrados
    suspend fun contarProductos(
        establecimientoId: Int
    ): Int {
        return productoDao.contarProductos(
            establecimientoId
        )
    }
}
