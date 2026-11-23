
package com.tiendasbarrio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiendasbarrio.data.repository.AbonoRepository

class AbonoViewModelFactory(
    private val repository: AbonoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                AbonoViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return AbonoViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
