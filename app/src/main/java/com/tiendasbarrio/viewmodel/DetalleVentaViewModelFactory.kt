
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiendasbarrio.data.repository.DetalleVentaRepository

class DetalleVentaViewModelFactory(
    private val repository: DetalleVentaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                DetalleVentaViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return DetalleVentaViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
