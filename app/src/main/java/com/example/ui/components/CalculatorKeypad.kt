package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NavyAccent
import com.example.ui.theme.PureWhite

sealed class CalcButtonType {
    data object Number : CalcButtonType()
    data object Operator : CalcButtonType()
    data object Utility : CalcButtonType()
    data object Equals : CalcButtonType()
}

@Composable
fun CalculatorKeypad(
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (String) -> Unit,
    onClear: () -> Unit,
    onPlusMinus: () -> Unit,
    onPercentage: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: AC, ±, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcKey(
                text = "AC",
                type = CalcButtonType.Utility,
                onClick = onClear,
                testTag = "btn_clear",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "±",
                type = CalcButtonType.Utility,
                onClick = onPlusMinus,
                testTag = "btn_plus_minus",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "%",
                type = CalcButtonType.Utility,
                onClick = onPercentage,
                testTag = "btn_percent",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "÷",
                type = CalcButtonType.Operator,
                onClick = { onOperator("÷") },
                testTag = "btn_divide",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcKey(
                text = "7",
                type = CalcButtonType.Number,
                onClick = { onDigit("7") },
                testTag = "btn_7",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "8",
                type = CalcButtonType.Number,
                onClick = { onDigit("8") },
                testTag = "btn_8",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "9",
                type = CalcButtonType.Number,
                onClick = { onDigit("9") },
                testTag = "btn_9",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "×",
                type = CalcButtonType.Operator,
                onClick = { onOperator("×") },
                testTag = "btn_multiply",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcKey(
                text = "4",
                type = CalcButtonType.Number,
                onClick = { onDigit("4") },
                testTag = "btn_4",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "5",
                type = CalcButtonType.Number,
                onClick = { onDigit("5") },
                testTag = "btn_5",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "6",
                type = CalcButtonType.Number,
                onClick = { onDigit("6") },
                testTag = "btn_6",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "−",
                type = CalcButtonType.Operator,
                onClick = { onOperator("−") },
                testTag = "btn_subtract",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcKey(
                text = "1",
                type = CalcButtonType.Number,
                onClick = { onDigit("1") },
                testTag = "btn_1",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "2",
                type = CalcButtonType.Number,
                onClick = { onDigit("2") },
                testTag = "btn_2",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "3",
                type = CalcButtonType.Number,
                onClick = { onDigit("3") },
                testTag = "btn_3",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "+",
                type = CalcButtonType.Operator,
                onClick = { onOperator("+") },
                testTag = "btn_add",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 5: 0, 00, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcKey(
                text = "0",
                type = CalcButtonType.Number,
                onClick = { onDigit("0") },
                testTag = "btn_0",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "00",
                type = CalcButtonType.Number,
                onClick = { onDigit("00") },
                testTag = "btn_00",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = ".",
                type = CalcButtonType.Number,
                onClick = onDecimal,
                testTag = "btn_decimal",
                modifier = Modifier.weight(1f)
            )
            CalcKey(
                text = "=",
                type = CalcButtonType.Equals,
                onClick = onEquals,
                testTag = "btn_equals",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalcKey(
    text: String,
    type: CalcButtonType,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val containerColor: Color
    val contentColor: Color
    val border: BorderStroke?
    val elevation = when (type) {
        CalcButtonType.Equals -> 4.dp
        CalcButtonType.Operator -> 3.dp
        CalcButtonType.Number -> 1.5.dp
        CalcButtonType.Utility -> 1.dp
    }

    when (type) {
        CalcButtonType.Number -> {
            containerColor = MaterialTheme.colorScheme.surface
            contentColor = MaterialTheme.colorScheme.onSurface
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        }
        CalcButtonType.Operator -> {
            containerColor = MaterialTheme.colorScheme.primary
            contentColor = MaterialTheme.colorScheme.onPrimary
            border = null
        }
        CalcButtonType.Utility -> {
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
        CalcButtonType.Equals -> {
            containerColor = NavyAccent
            contentColor = PureWhite
            border = null
        }
    }

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 58.dp, max = 70.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.filledTonalButtonElevation(
            defaultElevation = elevation,
            pressedElevation = 0.dp
        ),
        border = border,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = if (text.length > 2) 20.sp else 24.sp,
            fontWeight = if (type == CalcButtonType.Equals || type == CalcButtonType.Operator) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
