
package com.tiendasbarrio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalles_venta",
    foreignKeys = [
        ForeignKey(
            entity = VentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["ventaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["ventaId"]),
        Index(value = ["productoId"])
    ]
)
data class DetalleVentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val ventaId: Int,

    val productoId: Int,

    val nombreProducto: String,

    val cantidad: Int,

    val precioUnitario: Long,

    val subtotal: Long
)
