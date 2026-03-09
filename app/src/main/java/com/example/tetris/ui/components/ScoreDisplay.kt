package com.example.tetris.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.ui.theme.AccentColor
import com.example.tetris.ui.theme.TextPrimary
import com.example.tetris.ui.theme.TextSecondary

@Composable
fun ScoreDisplay(
    score: Long,
    lines: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatItem("SCORE", score.toString())
        Spacer(modifier = Modifier.height(8.dp))
        StatItem("LINES", lines.toString())
        Spacer(modifier = Modifier.height(8.dp))
        StatItem("LEVEL", level.toString())
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = AccentColor,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
