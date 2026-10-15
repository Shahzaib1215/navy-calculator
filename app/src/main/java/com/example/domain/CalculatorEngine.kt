package com.example.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

object CalculatorEngine {

    private val decimalFormat = DecimalFormat("#,##0.##########", DecimalFormatSymbols(Locale.US)).apply {
        maximumFractionDigits = 10
    }

    private val scientificFormat = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        // Handle very large or very small absolute non-zero numbers
        val absVal = abs(value)
        return if ((absVal >= 1e12 || (absVal < 1e-6 && absVal > 0))) {
            scientificFormat.format(value)
        } else {
            // Trim precision artifacts like 0.1 + 0.2 = 0.30000000000000004
            val rounded = BigDecimal(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
            val doubleVal = rounded.toDouble()
            decimalFormat.format(doubleVal)
        }
    }

    fun evaluate(rawExpression: String): Result<Double> {
        val sanitized = sanitize(rawExpression)
        if (sanitized.isBlank()) return Result.failure(IllegalArgumentException("Empty expression"))

        return try {
            val parser = Parser(sanitized)
            val result = parser.parse()
            if (result.isNaN() || result.isInfinite()) {
                if (result.isInfinite()) {
                    Result.failure(ArithmeticException("Cannot divide by zero"))
                } else {
                    Result.failure(ArithmeticException("Invalid computation"))
                }
            } else {
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sanitize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .replace(",", "")
            .trim()
    }

    /**
     * Recursive descent parser for evaluating arithmetic and scientific expressions.
     * Grammar:
     *   expression = term (('+' | '-') term)*
     *   term       = factor (('*' | '/' | '%') factor)*
     *   factor     = base ('^' factor)?
     *   base       = ('+' | '-')? primary
     *   primary    = NUMBER | '(' expression ')' | FUNCTION '(' expression ')' | '√' factor
     */
    private class Parser(private val input: String) {
        private var pos = 0
        private val length = input.length

        fun parse(): Double {
            val res = parseExpression()
            skipWhitespace()
            if (pos < length) {
                // If there's an unparsed closing paren or extra token
                throw IllegalArgumentException("Unexpected token at position $pos: '${input[pos]}'")
            }
            return res
        }

        private fun parseExpression(): Double {
            var left = parseTerm()
            while (true) {
                skipWhitespace()
                if (match('+')) {
                    left += parseTerm()
                } else if (match('-')) {
                    left -= parseTerm()
                } else {
                    break
                }
            }
            return left
        }

        private fun parseTerm(): Double {
            var left = parseFactor()
            while (true) {
                skipWhitespace()
                if (match('*')) {
                    left *= parseFactor()
                } else if (match('/')) {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Cannot divide by zero")
                    left /= divisor
                } else if (match('%')) {
                    // Modulo or percentage calculation
                    left = (left / 100.0)
                } else {
                    break
                }
            }
            return left
        }

        private fun parseFactor(): Double {
            val base = parseUnary()
            skipWhitespace()
            if (match('^')) {
                val exponent = parseFactor() // Right associative
                return base.pow(exponent)
            }
            return base
        }

        private fun parseUnary(): Double {
            skipWhitespace()
            if (match('+')) {
                return parseUnary()
            }
            if (match('-')) {
                return -parseUnary()
            }
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            skipWhitespace()
            if (pos >= length) throw IllegalArgumentException("Unexpected end of expression")

            // Square root operator: √
            if (match('√') || matchString("sqrt")) {
                val value = if (peek() == '(') {
                    consume('(')
                    val v = parseExpression()
                    consume(')')
                    v
                } else {
                    parsePrimary()
                }
                if (value < 0) throw ArithmeticException("Square root of negative number")
                return sqrt(value)
            }

            // Functions: sin, cos, tan, ln, log
            if (matchString("sin")) {
                consume('(')
                val v = parseExpression()
                consume(')')
                return sin(Math.toRadians(v))
            }
            if (matchString("cos")) {
                consume('(')
                val v = parseExpression()
                consume(')')
                return cos(Math.toRadians(v))
            }
            if (matchString("tan")) {
                consume('(')
                val v = parseExpression()
                consume(')')
                return tan(Math.toRadians(v))
            }
            if (matchString("ln")) {
                consume('(')
                val v = parseExpression()
                consume(')')
                if (v <= 0) throw ArithmeticException("Logarithm of non-positive number")
                return ln(v)
            }
            if (matchString("log")) {
                consume('(')
                val v = parseExpression()
                consume(')')
                if (v <= 0) throw ArithmeticException("Logarithm of non-positive number")
                return log10(v)
            }

            // Parentheses
            if (match('(')) {
                val value = parseExpression()
                consume(')')
                return value
            }

            // Number (including decimals and scientific notation like 1e5)
            val start = pos
            if (pos < length && (input[pos].isDigit() || input[pos] == '.')) {
                var hasDot = false
                while (pos < length) {
                    val c = input[pos]
                    if (c == '.') {
                        if (hasDot) break
                        hasDot = true
                        pos++
                    } else if (c.isDigit()) {
                        pos++
                    } else if (c == 'E' || c == 'e') {
                        pos++
                        if (pos < length && (input[pos] == '+' || input[pos] == '-')) {
                            pos++
                        }
                    } else {
                        break
                    }
                }
                val numStr = input.substring(start, pos)
                return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
            }

            throw IllegalArgumentException("Unexpected character '${input[pos]}' at position $pos")
        }

        private fun skipWhitespace() {
            while (pos < length && input[pos].isWhitespace()) {
                pos++
            }
        }

        private fun peek(): Char = if (pos < length) input[pos] else '\u0000'

        private fun match(expected: Char): Boolean {
            skipWhitespace()
            if (pos < length && input[pos] == expected) {
                pos++
                return true
            }
            return false
        }

        private fun matchString(expected: String): Boolean {
            skipWhitespace()
            if (input.startsWith(expected, pos)) {
                pos += expected.length
                return true
            }
            return false
        }

        private fun consume(expected: Char) {
            skipWhitespace()
            if (pos < length && input[pos] == expected) {
                pos++
            } else {
                throw IllegalArgumentException("Expected '$expected' but found '${peek()}' at position $pos")
            }
        }
    }
}
