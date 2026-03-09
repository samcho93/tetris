package com.example.tetris.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ControlPad(
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onRotate: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: directional controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RepeatableButton(
                label = "\u25C0",
                onClick = onLeft,
                repeatable = true
            )
            RepeatableButton(
                label = "\u25BC",
                onClick = onSoftDrop,
                repeatable = true
            )
            RepeatableButton(
                label = "\u25B6",
                onClick = onRight,
                repeatable = true
            )
        }

        // Right side: action controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                label = "\u21BB",
                subLabel = "ROTATE",
                onClick = onRotate,
                color = Color(0xFF4A90D9)
            )
            DropButton(
                onHardDrop = onHardDrop,
                onSoftDrop = onSoftDrop,
                color = Color(0xFFD94A4A)
            )
        }
    }
}

@Composable
private fun RepeatableButton(
    label: String,
    onClick: () -> Unit,
    repeatable: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF2A2A3E))
            .pointerInput(Unit) {
                if (repeatable) {
                    detectTapGestures(
                        onPress = {
                            onClick()
                            coroutineScope {
                                val repeatJob = launch {
                                    delay(200)
                                    while (true) {
                                        delay(60)
                                        onClick()
                                    }
                                }
                                tryAwaitRelease()
                                repeatJob.cancel()
                            }
                        }
                    )
                } else {
                    detectTapGestures(onTap = { onClick() })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 22.sp
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    subLabel: String,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.8f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onClick() })
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = subLabel,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 8.sp
        )
    }
}

/**
 * Drop button with dual behavior:
 * - Short tap (< 180ms): instant hard drop
 * - Long press (>= 180ms): rapid soft drop while held, normal speed on release
 */
@Composable
private fun DropButton(
    onHardDrop: () -> Unit,
    onSoftDrop: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.8f))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            var isLongPress = false
                            coroutineScope {
                                val fastDropJob = launch {
                                    delay(180)
                                    isLongPress = true
                                    while (true) {
                                        onSoftDrop()
                                        delay(30)
                                    }
                                }
                                tryAwaitRelease()
                                fastDropJob.cancel()
                                if (!isLongPress) {
                                    onHardDrop()
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u2B07",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "DROP",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 8.sp
        )
    }
}
