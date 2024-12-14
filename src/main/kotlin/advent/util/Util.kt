package advent.util

import java.io.File
import java.text.NumberFormat
import java.util.*

fun readAllLines(fileName: String): List<String> {
    val allLines =
        File("C:\\Users\\mathi\\Desktop\\cursor_projects\\advent-of-code-2024\\src\\main\\resources\\$fileName")
            .readLines()
    return allLines
}

fun getCharMap(lines: List<String>): Array<Array<Char>> {
    val nbColumns = lines[0].length
    val nbLines = lines.size
    val charMap = Array(nbColumns) { _ -> Array(nbLines) { _ -> ' ' } }
    lines.forEachIndexed { lineIndex, string ->
        string.asSequence().withIndex()
            .forEach { charIndexed -> charMap[charIndexed.index][lineIndex] = charIndexed.value }
    }
    return charMap
}

fun toCurrency(price: Long): String =
    NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 0
        currency = Currency.getInstance("EUR");
    }.format(price)