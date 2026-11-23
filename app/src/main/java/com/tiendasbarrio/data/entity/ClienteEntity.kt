
package com.tiendasbarrio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clientes",
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
            value = ["establecimientoId", "documento"],
            unique = true
        )
    ]
)
data class ClienteEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val establecimientoId: Int,

    val documento: String,

    val nombre: String,

    val telefono: String,

    val direccion: String,

    val correo: String,

    val fechaRegistro: Long = System.currentTimeMillis(),

    val activo: Boolean = true
)
