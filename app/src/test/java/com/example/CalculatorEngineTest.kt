package com.example

import com.example.domain.CalculatorEngine
import org.junit.Assert.*
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun testBasicArithmetic() {
        val res1 = CalculatorEngine.evaluate("2 + 3").getOrNull()
        assertEquals(5.0, res1!!, 0.0001)

        val res2 = CalculatorEngine.evaluate("10 − 4").getOrNull()
        assertEquals(6.0, res2!!, 0.0001)

        val res3 = CalculatorEngine.evaluate("6 × 7").getOrNull()
        assertEquals(42.0, res3!!, 0.0001)

        val res4 = CalculatorEngine.evaluate("48 ÷ 6").getOrNull()
        assertEquals(8.0, res4!!, 0.0001)
    }

    @Test
    fun testOperatorPrecedenceAndParentheses() {
        // Standard precedence: multiplication before addition
        val res1 = CalculatorEngine.evaluate("2 + 3 × 4").getOrNull()
        assertEquals(14.0, res1!!, 0.0001)

        // Parentheses override precedence
        val res2 = CalculatorEngine.evaluate("(2 + 3) × 4").getOrNull()
        assertEquals(20.0, res2!!, 0.0001)
    }

    @Test
    fun testPowerAndSquareRoot() {
        val res1 = CalculatorEngine.evaluate("2^3").getOrNull()
        assertEquals(8.0, res1!!, 0.0001)

        val res2 = CalculatorEngine.evaluate("√(16)").getOrNull()
        assertEquals(4.0, res2!!, 0.0001)
    }

    @Test
    fun testDivideByZero() {
        val result = CalculatorEngine.evaluate("10 ÷ 0")
        assertTrue(result.isFailure)
        assertEquals("Cannot divide by zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun testFormatResult() {
        assertEquals("42", CalculatorEngine.formatResult(42.0))
        assertEquals("1,234.5", CalculatorEngine.formatResult(1234.5))
        assertEquals("0.3", CalculatorEngine.formatResult(0.1 + 0.2))
    }
}
