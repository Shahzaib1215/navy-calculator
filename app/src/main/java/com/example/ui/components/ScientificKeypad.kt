package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IceNavy100
import com.example.ui.theme.IceNavy200

@Composable
fun ScientificKeypad(
    onFunction: (String) -> Unit,
    onParenthesis: () -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: Memory keys
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScientificButton(label = "MC", onClick = onMemoryClear, modifier = Modifier.weight(1f), testTag = "btn_mc")
                ScientificButton(label = "MR", onClick = onMemoryRecall, modifier = Modifier.weight(1f), testTag = "btn_mr")
                ScientificButton(label = "M+", onClick = onMemoryAdd, modifier = Modifier.weight(1f), testTag = "btn_m_plus")
                ScientificButton(label = "M−", onClick = onMemorySubtract, modifier = Modifier.weight(1f), testTag = "btn_m_minus")
                ScientificButton(label = "( )", onClick = onParenthesis, modifier = Modifier.weight(1f), testTag = "btn_paren")
            }

            // Row 2: Trig & Log
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScientificButton(label = "sin", onClick = { onFunction("sin") }, modifier = Modifier.weight(1f), testTag = "btn_sin")
                ScientificButton(label = "cos", onClick = { onFunction("cos") }, modifier = Modifier.weight(1f), testTag = "btn_cos")
                ScientificButton(label = "tan", onClick = { onFunction("tan") }, modifier = Modifier.weight(1f), testTag = "btn_tan")
                ScientificButton(label = "ln", onClick = { onFunction("ln") }, modifier = Modifier.weight(1f), testTag = "btn_ln")
                ScientificButton(label = "log", onClick = { onFunction("log") }, modifier = Modifier.weight(1f), testTag = "btn_log")
            }

            // Row 3: Powers & Constants
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScientificButton(label = "√", onClick = { onFunction("√") }, modifier = Modifier.weight(1f), testTag = "btn_sqrt")
                ScientificButton(label = "x²", onClick = { onFunction("x²") }, modifier = Modifier.weight(1f), testTag = "btn_square")
                ScientificButton(label = "xʸ", onClick = { onFunction("^") }, modifier = Modifier.weight(1f), testTag = "btn_power")
                ScientificButton(label = "π", onClick = { onFunction("π") }, modifier = Modifier.weight(1f), testTag = "btn_pi")
                ScientificButton(label = "e", onClick = { onFunction("e") }, modifier = Modifier.weight(1f), testTag = "btn_e")
            }
        }
    }
}

@Composable
private fun ScientificButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .height(42.dp)
            .testTag(testTag),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.filledTonalButtonElevation(
            defaultElevation = 0.5.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
