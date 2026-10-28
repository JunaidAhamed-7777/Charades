package com.yourdomain.freecharades.domain.usecase

import com.yourdomain.freecharades.data.repository.CategoryRepository

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke() = repository.load()
}
