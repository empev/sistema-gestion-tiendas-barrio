
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiendasbarrio.data.repository.VentaCompletaRepository

class VentaCompletaViewModelFactory(
    private val repository: VentaCompletaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                VentaCompletaViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return VentaCompletaViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
