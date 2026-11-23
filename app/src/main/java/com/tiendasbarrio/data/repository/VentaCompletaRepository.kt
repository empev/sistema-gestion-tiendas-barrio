
package com.tiendasbarrio.data.repository

import com.tiendasbarrio.data.entity.VentaEntity
import com.tiendasbarrio.data.entity.DetalleVentaEntity
import com.tiendasbarrio.data.entity.AbonoEntity
import com.tiendasbarrio.data.local.TiendasBarrioDatabase

class VentaCompletaRepository(
    private val database: TiendasBarrioDatabase
) {

    suspend fun registrarVentaCompleta(
        venta: VentaEntity,
        detalles: List<DetalleVentaEntity>,
        abonoInicial: AbonoEntity?
    ): Long {
        return database.registrarVentaCompleta(
            venta = venta,
            detalles = detalles,
            abonoInicial = abonoInicial
        )
    }

    suspend fun registrarAbonoCompleto(
        ventaId: Int,
        abono: AbonoEntity
    ) {
        database.registrarAbonoCompleto(
            ventaId = ventaId,
            abono = abono
        )
    }
}
