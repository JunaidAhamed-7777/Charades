package com.yourdomain.freecharades.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourdomain.freecharades.data.repository.CategoryRepository
import com.yourdomain.freecharades.domain.model.Category
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CatalogState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

open class CatalogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CategoryRepository.get(application)
    private val mutableCatalog = MutableStateFlow(CatalogState())
    val catalog: StateFlow<CatalogState> = mutableCatalog.asStateFlow()
    val categories = catalog.map { it.categories }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val loading = catalog.map { it.isLoading }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val error = catalog.map { it.error }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init { retry() }

    fun retry() {
        viewModelScope.launch {
            mutableCatalog.value = CatalogState()
            try {
                mutableCatalog.value = CatalogState(isLoading = false, categories = repository.load())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableCatalog.value = CatalogState(
                    isLoading = false,
                    error = "The installed photo library could not be opened. Reinstall the complete APK, then try again."
                )
            }
        }
    }
}
