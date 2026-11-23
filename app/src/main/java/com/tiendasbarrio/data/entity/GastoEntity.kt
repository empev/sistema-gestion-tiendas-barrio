
package com.tiendasbarrio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gastos",
    foreignKeys = [
        ForeignKey(
            entity = EstablecimientoEntity::class,
            parentColumns = ["id"],
            childColumns = ["establecimientoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["establecimientoId"])
    ]
)
data class GastoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val establecimientoId: Int,

    val categoria: String,

    val descripcion: String,

    val monto: Long,

    val fechaGasto: Long = System.currentTimeMillis()
)
