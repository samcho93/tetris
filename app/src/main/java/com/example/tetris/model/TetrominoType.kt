package com.example.tetris.model

import androidx.compose.ui.graphics.Color
import com.example.tetris.ui.theme.*

enum class TetrominoType(val color: Color) {
    I(CyanPiece),
    O(YellowPiece),
    T(PurplePiece),
    S(GreenPiece),
    Z(RedPiece),
    J(BluePiece),
    L(OrangePiece);
}
