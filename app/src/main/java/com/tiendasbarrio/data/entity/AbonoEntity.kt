
package com.tiendasbarrio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "abonos",
    foreignKeys = [
        ForeignKey(
            entity = VentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["ventaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ventaId"]),
        Index(value = ["fechaAbono"])
    ]
)
data class AbonoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val ventaId: Int,

    val monto: Long,

    val fechaAbono: Long = System.currentTimeMillis(),

    val observacion: String = ""
)
