
package com.tiendasbarrio.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "establecimientos")
data class EstablecimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val propietario: String,
    val municipio: String,
    val direccion: String,
    val telefono: String,
    val activo: Boolean = false
)
