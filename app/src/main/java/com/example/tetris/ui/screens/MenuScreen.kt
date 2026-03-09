package com.example.tetris.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.ui.theme.AccentColor
import com.example.tetris.ui.theme.MenuBackground
import com.example.tetris.ui.theme.TextPrimary
import com.example.tetris.ui.theme.TextSecondary

@Composable
fun MenuScreen(
    onStartGame: (level: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenuBackground)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "INFINITE\nTETRIS",
            color = AccentColor,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            lineHeight = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "endless block stacking",
            color = TextSecondary,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "SELECT SPEED",
            color = TextPrimary,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(24.dp))

        LevelButton(level = 1, label = "SLOW", description = "1.0s per drop", onClick = { onStartGame(1) })
        Spacer(modifier = Modifier.height(12.dp))
        LevelButton(level = 2, label = "NORMAL", description = "0.5s per drop", onClick = { onStartGame(2) })
        Spacer(modifier = Modifier.height(12.dp))
        LevelButton(level = 3, label = "FAST", description = "0.25s per drop", onClick = { onStartGame(3) })
    }
}

@Composable
private fun LevelButton(
    level: Int,
    label: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E1E3A))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lv.$level",
            color = AccentColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
