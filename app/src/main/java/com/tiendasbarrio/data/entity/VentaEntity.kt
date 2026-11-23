
package com.tiendasbarrio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(
            entity = EstablecimientoEntity::class,
            parentColumns = ["id"],
            childColumns = ["establecimientoId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["establecimientoId"]),
        Index(value = ["clienteId"]),
        Index(value = ["fechaVenta"])
    ]
)
data class VentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val establecimientoId: Int,

    val clienteId: Int? = null,

    val tipoVenta: String,

    val total: Long,

    val totalAbonado: Long = 0,

    val fechaVenta: Long = System.currentTimeMillis(),

    val observacion: String = ""
)
