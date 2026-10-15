package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationHistory
import com.example.data.repository.CalculatorRepository
import com.example.domain.CalculatorEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ReuseMode {
    RESULT,
    EXPRESSION
}

data class CalculatorUiState(
    val expression: String = "",
    val livePreview: String = "",
    val displayedResult: String = "0",
    val isEvaluated: Boolean = false,
    val errorMessage: String? = null,
    val isScientificExpanded: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val historySearchQuery: String = "",
    val showFavoritesOnly: Boolean = false,
    val memoryValue: Double = 0.0,
    val themeMode: Boolean? = null // null = system, false = light/white, true = dark navy
)

class CalculatorViewModel(
    private val repository: CalculatorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _allHistory = repository.allHistory
    private val _favHistory = repository.favoriteHistory

    val historyItems: StateFlow<List<CalculationHistory>> = combine(
        _uiState.map { it.showFavoritesOnly }.distinctUntilChanged(),
        _uiState.map { it.historySearchQuery }.distinctUntilChanged(),
        _allHistory,
        _favHistory
    ) { favOnly, query, all, favs ->
        val source = if (favOnly) favs else all
        if (query.isBlank()) {
            source
        } else {
            source.filter {
                it.expression.contains(query, ignoreCase = true) ||
                        it.result.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDigit(digit: String) {
        val current = _uiState.value
        val newExpr = if (current.isEvaluated) {
            digit
        } else {
            current.expression + digit
        }
        updateExpression(newExpr, isEvaluated = false)
    }

    fun onDecimal() {
        val current = _uiState.value
        val expr = if (current.isEvaluated) "0." else current.expression
        if (expr.isEmpty()) {
            updateExpression("0.", isEvaluated = false)
            return
        }

        // Check if the current number segment already has a dot
        val tokens = expr.split(Regex("[+\\-×÷^()%]"))
        val lastToken = tokens.lastOrNull() ?: ""
        if (!lastToken.contains('.')) {
            updateExpression(expr + ".", isEvaluated = false)
        }
    }

    fun onOperator(op: String) {
        val current = _uiState.value
        val baseExpr = if (current.isEvaluated) {
            // Start chaining from the last result
            current.displayedResult.replace(",", "")
        } else {
            current.expression
        }

        if (baseExpr.isEmpty()) {
            if (op == "−") {
                updateExpression("−", isEvaluated = false)
            }
            return
        }

        val lastChar = baseExpr.last()
        val newExpr = when {
            isOperator(lastChar) -> baseExpr.dropLast(1) + op
            lastChar == '(' -> if (op == "−") baseExpr + op else baseExpr
            else -> baseExpr + op
        }
        updateExpression(newExpr, isEvaluated = false)
    }

    fun onParenthesis() {
        val current = _uiState.value
        val expr = if (current.isEvaluated) "" else current.expression
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }

        val newExpr = if (expr.isEmpty() || isOperator(expr.last()) || expr.last() == '(') {
            expr + "("
        } else if (openCount > closeCount) {
            expr + ")"
        } else {
            expr + "×("
        }
        updateExpression(newExpr, isEvaluated = false)
    }

    fun onFunction(func: String) {
        val current = _uiState.value
        val expr = if (current.isEvaluated) "" else current.expression
        val newExpr = when (func) {
            "√" -> if (expr.isNotEmpty() && !isOperator(expr.last()) && expr.last() != '(') "$expr×√(" else "$expr√("
            "sin" -> "$expr" + (if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "×sin(" else "sin(")
            "cos" -> "$expr" + (if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "×cos(" else "cos(")
            "tan" -> "$expr" + (if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "×tan(" else "tan(")
            "ln" -> "$expr" + (if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "×ln(" else "ln(")
            "log" -> "$expr" + (if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "×log(" else "log(")
            "π" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "${expr}×π" else "${expr}π"
            "e" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "${expr}×e" else "${expr}e"
            "x²" -> if (expr.isNotEmpty() && !isOperator(expr.last())) "$expr^2" else expr
            "^" -> if (expr.isNotEmpty() && !isOperator(expr.last())) "$expr^" else expr
            else -> expr
        }
        updateExpression(newExpr, isEvaluated = false)
    }

    fun onPercentage() {
        val current = _uiState.value
        val expr = current.expression
        if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
            updateExpression("$expr%", isEvaluated = false)
        }
    }

    fun onPlusMinus() {
        val current = _uiState.value
        if (current.isEvaluated) {
            val num = current.displayedResult.replace(",", "").toDoubleOrNull() ?: return
            val toggled = -num
            val formatted = CalculatorEngine.formatResult(toggled)
            _uiState.update {
                it.copy(
                    expression = formatted,
                    displayedResult = formatted,
                    livePreview = ""
                )
            }
            return
        }

        val expr = current.expression
        if (expr.isEmpty()) {
            updateExpression("−", isEvaluated = false)
            return
        }

        // Find the last number token
        val lastOpIndex = expr.lastIndexOfAny(charArrayOf('+', '−', '×', '÷', '(', '^'))
        if (lastOpIndex == -1) {
            // Entire expression is a single number
            val newExpr = if (expr.startsWith("−")) expr.substring(1) else "−$expr"
            updateExpression(newExpr, isEvaluated = false)
        } else {
            val prefix = expr.substring(0, lastOpIndex + 1)
            val suffix = expr.substring(lastOpIndex + 1)
            val newSuffix = if (suffix.startsWith("−")) suffix.substring(1) else "−$suffix"
            updateExpression(prefix + newSuffix, isEvaluated = false)
        }
    }

    fun onBackspace() {
        val current = _uiState.value
        if (current.isEvaluated) {
            // Reset to 0
            _uiState.update { it.copy(expression = "", displayedResult = "0", livePreview = "", isEvaluated = false) }
            return
        }
        if (current.expression.isNotEmpty()) {
            // Check multi-character functions to delete in one go (sin(, cos(, tan(, ln(, log(, etc.)
            var expr = current.expression
            val multiChars = listOf("sin(", "cos(", "tan(", "log(", "ln(", "√(")
            val matchedPrefix = multiChars.firstOrNull { expr.endsWith(it) }
            expr = if (matchedPrefix != null) {
                expr.dropLast(matchedPrefix.length)
            } else {
                expr.dropLast(1)
            }
            updateExpression(expr, isEvaluated = false)
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                livePreview = "",
                displayedResult = "0",
                errorMessage = null,
                isEvaluated = false
            )
        }
    }

    fun onEquals() {
        val current = _uiState.value
        val expr = current.expression.trim()
        if (expr.isEmpty()) return

        // Auto-close open parentheses
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        val balancedExpr = if (openCount > closeCount) {
            expr + ")".repeat(openCount - closeCount)
        } else {
            expr
        }

        val evalResult = CalculatorEngine.evaluate(balancedExpr)
        evalResult.fold(
            onSuccess = { value ->
                val formattedResult = CalculatorEngine.formatResult(value)
                _uiState.update {
                    it.copy(
                        expression = balancedExpr,
                        displayedResult = formattedResult,
                        livePreview = "",
                        errorMessage = null,
                        isEvaluated = true
                    )
                }
                // Save to Room history log
                viewModelScope.launch {
                    repository.saveCalculation(expression = balancedExpr, result = formattedResult)
                }
            },
            onFailure = { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = error.localizedMessage ?: "Invalid calculation",
                        livePreview = ""
                    )
                }
            }
        )
    }

    fun reuseHistoryItem(item: CalculationHistory, mode: ReuseMode) {
        when (mode) {
            ReuseMode.RESULT -> {
                val cleanResult = item.result.replace(",", "")
                updateExpression(cleanResult, isEvaluated = true)
                _uiState.update {
                    it.copy(
                        displayedResult = item.result,
                        isHistoryOpen = false
                    )
                }
            }
            ReuseMode.EXPRESSION -> {
                updateExpression(item.expression, isEvaluated = false)
                _uiState.update {
                    it.copy(
                        displayedResult = item.result,
                        isHistoryOpen = false
                    )
                }
            }
        }
    }

    fun toggleFavorite(item: CalculationHistory) {
        viewModelScope.launch {
            repository.toggleFavorite(item.id, !item.isFavorite)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(historySearchQuery = query) }
    }

    fun toggleFavoritesOnly() {
        _uiState.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
    }

    fun setScientificExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isScientificExpanded = expanded) }
    }

    fun setHistoryOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = isOpen) }
    }

    fun setThemeMode(isDark: Boolean?) {
        _uiState.update { it.copy(themeMode = isDark) }
    }

    // Memory Functions
    fun memoryAdd() {
        val current = _uiState.value
        val value = current.displayedResult.replace(",", "").toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(memoryValue = it.memoryValue + value) }
    }

    fun memorySubtract() {
        val current = _uiState.value
        val value = current.displayedResult.replace(",", "").toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(memoryValue = it.memoryValue - value) }
    }

    fun memoryClear() {
        _uiState.update { it.copy(memoryValue = 0.0) }
    }

    fun memoryRecall() {
        val mem = _uiState.value.memoryValue
        val formatted = CalculatorEngine.formatResult(mem)
        val currentExpr = _uiState.value.expression
        val newExpr = if (currentExpr.isEmpty() || isOperator(currentExpr.last())) {
            currentExpr + formatted
        } else {
            formatted
        }
        updateExpression(newExpr, isEvaluated = false)
    }

    private fun updateExpression(expr: String, isEvaluated: Boolean) {
        // Calculate live preview if feasible
        val preview = if (!isEvaluated && expr.isNotBlank() && !isOperator(expr.last())) {
            // Auto close parens for preview evaluation
            val open = expr.count { it == '(' }
            val close = expr.count { it == ')' }
            val balanced = if (open > close) expr + ")".repeat(open - close) else expr
            val res = CalculatorEngine.evaluate(balanced)
            res.getOrNull()?.let { CalculatorEngine.formatResult(it) } ?: ""
        } else {
            ""
        }

        _uiState.update {
            it.copy(
                expression = expr,
                livePreview = preview,
                displayedResult = if (isEvaluated) it.displayedResult else if (expr.isEmpty()) "0" else it.displayedResult,
                errorMessage = null,
                isEvaluated = isEvaluated
            )
        }
    }

    private fun isOperator(c: Char): Boolean = c == '+' || c == '−' || c == '×' || c == '÷' || c == '^'
}

class CalculatorViewModelFactory(
    private val repository: CalculatorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
