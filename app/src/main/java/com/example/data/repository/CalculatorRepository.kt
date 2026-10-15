package com.example.data.repository

import com.example.data.local.CalculationHistory
import com.example.data.local.HistoryDao
import kotlinx.coroutines.flow.Flow

class CalculatorRepository(private val dao: HistoryDao) {
    val allHistory: Flow<List<CalculationHistory>> = dao.getAllHistory()
    val favoriteHistory: Flow<List<CalculationHistory>> = dao.getFavoriteHistory()

    suspend fun saveCalculation(expression: String, result: String): Long {
        return dao.insert(CalculationHistory(expression = expression, result = result))
    }

    suspend fun deleteHistory(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAllHistory() {
        dao.clearAll()
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }
}
