
package com.tiendasbarrio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = EstablecimientoEntity::class,
            parentColumns = ["id"],
            childColumns = ["establecimientoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["establecimientoId"]),
        Index(
            value = ["establecimientoId", "codigo"],
            unique = true
        )
    ]
)
data class ProductoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val establecimientoId: Int,

    val codigo: String,

    val nombre: String,

    val categoria: String,

    val descripcion: String,

    val precioCompra: Long,

    val precioVenta: Long,

    val existencia: Int,

    val stockMinimo: Int,

    val fechaRegistro: Long = System.currentTimeMillis()
)
