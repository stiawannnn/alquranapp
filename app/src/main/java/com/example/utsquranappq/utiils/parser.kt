package com.example.utsquranappq.utiils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun parseTajweedText(text: String): AnnotatedString {
    return buildAnnotatedString {
        // Mapping warna berdasarkan tabel TajweedParser
        val colorMap = mapOf(
            "h" to Color(0xFFAAAAAA), // Hamza-wasl
            "s" to Color(0xFFAAAAAA), // Silent
            "l" to Color(0xFFAAAAAA), // Laam-shamsiyah
            "n" to Color(0xFF537FFF), // Madda-normal
            "p" to Color(0xFF4050FF), // Madda-permissible
            "m" to Color(0xFF000EBC), // Madda-necessary
            "q" to Color(0xFFDD0008), // Qalaqah
            "o" to Color(0xFF2144C1), // Madda-obligatory
            "c" to Color(0xFFD500B7), // Ikhafa-shafawi
            "f" to Color(0xFF9400A8), // Ikhafa
            "w" to Color(0xFF58B800), // Idgham-shafawi
            "i" to Color(0xFF26BFFD), // Iqlab
            "a" to Color(0xFF169777), // Idgham with Ghunnah
            "u" to Color(0xFF169200), // Idgham without Ghunnah
            "d" to Color(0xFFA1A1A1), // Idgham-mutajanisayn
            "b" to Color(0xFFA1A1A1), // Idgham-mutaqaribayn
            "g" to Color(0xFFFF7E1E)  // Ghunna
        )

        // Regex untuk mendeteksi semua pola tajweed
        val pattern = Regex("\\[([a-z])(?::\\d+)?\\[(.*?)]")
        var lastIndex = 0

        pattern.findAll(text).forEach { match ->
            val rule = match.groupValues[1] // Identifier (h, s, l, dll.)
            val content = match.groupValues[2] // Teks dalam anotasi
            val start = match.range.first
            val end = match.range.last + 1

            // Tambahkan teks sebelum anotasi
            if (lastIndex < start) {
                val beforeText = text.substring(lastIndex, start)
                append(beforeText)
                Log.d("ParseTajweed", "Before annotation: '$beforeText'")
            }

            // Terapkan warna hanya pada teks dalam anotasi
            colorMap[rule]?.let { color ->
                withStyle(style = SpanStyle(color = color)) {
                    append(content)
                    Log.d("ParseTajweed", "Styled text: '$content' with color $color")
                }
            } ?: run {
                append(content) // Jika tidak ada warna, tambahkan tanpa styling
                Log.d("ParseTajweed", "Unstyled text: '$content'")
            }

            lastIndex = end
        }

        // Tambahkan sisa teks setelah anotasi terakhir
        if (lastIndex < text.length) {
            val remainingText = text.substring(lastIndex)
            append(remainingText)
            Log.d("ParseTajweed", "Remaining text: '$remainingText'")
        }

        // Log hasil akhir untuk debugging
        Log.d("ParseTajweed", "Final output: '${toString()}'")
    }
}