package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.DisplayPanel
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.ScientificKeypad
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyItems by viewModel.historyItems.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Navy Calculator",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "White & Navy Blue Edition",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    // Scientific fx toggle
                    FilledIconToggleButton(
                        checked = uiState.isScientificExpanded,
                        onCheckedChange = { viewModel.setScientificExpanded(it) },
                        modifier = Modifier.testTag("btn_toggle_scientific"),
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Functions,
                            contentDescription = "Toggle Scientific Functions"
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // History Log toggle with badge
                    BadgedBox(
                        badge = {
                            if (historyItems.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = if (historyItems.size > 99) "99+" else historyItems.size.toString()
                                    )
                                }
                            }
                        }
                    ) {
                        FilledTonalIconButton(
                            onClick = { viewModel.setHistoryOpen(true) },
                            modifier = Modifier.testTag("btn_open_history"),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = "Open Calculation History"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Theme toggle (Light / Dark Navy)
                    IconButton(
                        onClick = {
                            val nextMode = when (uiState.themeMode) {
                                null -> true // switch to dark navy
                                true -> false // switch to white light
                                false -> null // back to auto
                            }
                            viewModel.setThemeMode(nextMode)
                        },
                        modifier = Modifier.testTag("btn_theme_toggle")
                    ) {
                        val icon = when (uiState.themeMode) {
                            true -> Icons.Default.DarkMode
                            false -> Icons.Default.LightMode
                            null -> Icons.Default.BrightnessAuto
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Section
            DisplayPanel(
                uiState = uiState,
                onBackspace = { viewModel.onBackspace() },
                modifier = Modifier.weight(1f, fill = false)
            )

            // Scientific Keypad (animated expand/collapse)
            AnimatedVisibility(
                visible = uiState.isScientificExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ScientificKeypad(
                    onFunction = { viewModel.onFunction(it) },
                    onParenthesis = { viewModel.onParenthesis() },
                    onMemoryClear = { viewModel.memoryClear() },
                    onMemoryRecall = { viewModel.memoryRecall() },
                    onMemoryAdd = { viewModel.memoryAdd() },
                    onMemorySubtract = { viewModel.memorySubtract() }
                )
            }

            // Main Calculator Keypad
            CalculatorKeypad(
                onDigit = { viewModel.onDigit(it) },
                onDecimal = { viewModel.onDecimal() },
                onOperator = { viewModel.onOperator(it) },
                onClear = { viewModel.onClear() },
                onPlusMinus = { viewModel.onPlusMinus() },
                onPercentage = { viewModel.onPercentage() },
                onEquals = { viewModel.onEquals() },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }

    // Calculation History Bottom Sheet
    if (uiState.isHistoryOpen) {
        HistoryBottomSheet(
            historyList = historyItems,
            searchQuery = uiState.historySearchQuery,
            showFavoritesOnly = uiState.showFavoritesOnly,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onToggleFavoritesOnly = { viewModel.toggleFavoritesOnly() },
            onReuseItem = { item, mode -> viewModel.reuseHistoryItem(item, mode) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onDeleteItem = { viewModel.deleteHistoryItem(it) },
            onClearAll = { viewModel.clearAllHistory() },
            onDismiss = { viewModel.setHistoryOpen(false) }
        )
    }
}
