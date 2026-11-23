
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiendasbarrio.data.repository.VentaRepository
import com.tiendasbarrio.data.repository.AbonoRepository

class ReporteViewModelFactory(
    private val ventaRepository: VentaRepository,
    private val abonoRepository: AbonoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ReporteViewModel::class.java)) {
            return ReporteViewModel(
                ventaRepository,
                abonoRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
