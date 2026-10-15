package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CalculationHistory
import com.example.data.local.CalculatorDatabase
import com.example.data.local.HistoryDao
import com.example.data.repository.CalculatorRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: CalculatorDatabase
    private lateinit var dao: HistoryDao
    private lateinit var repository: CalculatorRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CalculatorDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.historyDao()
        repository = CalculatorRepository(dao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Navy Calculator", appName)
    }

    @Test
    fun testSaveAndRetrieveCalculationHistory() = runBlocking {
        repository.saveCalculation("15 × 8", "120")
        repository.saveCalculation("100 + 25", "125")

        val history = repository.allHistory.first()
        assertEquals(2, history.size)
        assertEquals("100 + 25", history[0].expression)
        assertEquals("125", history[0].result)

        // Toggle favorite
        val firstId = history[0].id
        repository.toggleFavorite(firstId, true)
        val favorites = repository.favoriteHistory.first()
        assertEquals(1, favorites.size)
        assertEquals(firstId, favorites[0].id)

        // Delete item
        repository.deleteHistory(firstId)
        val updated = repository.allHistory.first()
        assertEquals(1, updated.size)
        assertEquals("15 × 8", updated[0].expression)

        // Clear all
        repository.clearAllHistory()
        val empty = repository.allHistory.first()
        assertTrue(empty.isEmpty())
    }
}
