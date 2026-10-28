package com.yourdomain.freecharades.domain.usecase

import com.yourdomain.freecharades.data.repository.CategoryRepository

class GetWordsForCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String) = repository.load().firstOrNull { it.id == categoryId }?.words.orEmpty()
}
