package com.cristo17.games.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// Colores Neón Premium
val NeonCyan = Color(0xFF00FBFF)
val NeonRed = Color(0xFFFF0055)
val NeonYellow = Color(0xFFFFEE00)
val NeonPurple = Color(0xFFBC00FF)

// 1. JUGADOR: Cubo con Visor y Núcleo de Energía
fun DrawScope.drawPlayer(x: Float, y: Float, size: Float) {
    // Brillo exterior (Glow)
    drawRoundRect(
        color = NeonYellow.copy(alpha = 0.2f),
        topLeft = Offset(x - 5f, y - 5f),
        size = Size(size + 10f, size + 10f),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Cuerpo con degradado radial (Núcleo)
    val bodyBrush = Brush.linearGradient(
        colors = listOf(NeonYellow, Color(0xFFFF6600)),
        start = Offset(x, y),
        end = Offset(x + size, y + size)
    )

    drawRoundRect(
        brush = bodyBrush,
        topLeft = Offset(x, y),
        size = Size(size, size),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Detalle de Visor Tecnológico
    drawRect(
        color = Color.Black.copy(alpha = 0.7f),
        topLeft = Offset(x + (size * 0.1f), y + (size * 0.25f)),
        size = Size(size * 0.8f, size * 0.25f)
    )
    
    // Brillo en el visor
    drawLine(
        color = Color.White.copy(alpha = 0.6f),
        start = Offset(x + (size * 0.15f), y + (size * 0.3f)),
        end = Offset(x + (size * 0.4f), y + (size * 0.3f)),
        strokeWidth = 3f
    )
}

// 2. PINCHO: Cristal de Energía Roja
fun DrawScope.drawSpike(x: Float, y: Float) {
    val path = Path().apply {
        moveTo(x + 25f, y - 60f) // Punta
        lineTo(x + 50f, y)      // Base derecha
        lineTo(x, y)           // Base izquierda
        close()
    }

    // Degradado de cristal
    val spikeBrush = Brush.verticalGradient(
        colors = listOf(Color.White, NeonRed, Color(0xFF440011)),
        startY = y - 60f,
        endY = y
    )

    drawPath(path, brush = spikeBrush)

    // Líneas de brillo internas
    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(x + 25f, y - 60f),
        end = Offset(x + 25f, y),
        strokeWidth = 2f
    )
}

// 3. PLATAFORMA: Diseño Tech con Cuadrícula y Luces
fun DrawScope.drawPlatform(x: Float, y: Float, width: Float) {
    val h = 20f
    
    // 1. Sombra de Brillo (Glow)
    drawRect(
        color = NeonCyan.copy(alpha = 0.15f),
        topLeft = Offset(x - 10f, y - 5f),
        size = Size(width + 20f, h + 10f)
    )

    // 2. Base metálica oscura
    drawRect(
        color = Color(0xFF111111),
        topLeft = Offset(x, y),
        size = Size(width, h)
    )

    // 3. Patrón de Cuadrícula (Grid) interna
    val step = width / 6
    for (i in 0..6) {
        drawLine(
            color = NeonCyan.copy(alpha = 0.3f),
            start = Offset(x + (i * step), y),
            end = Offset(x + (i * step), y + h),
            strokeWidth = 1f
        )
    }

    // 4. Bordes de Neón (Luces)
    // Superior
    drawLine(
        color = NeonCyan,
        start = Offset(x, y),
        end = Offset(x + width, y),
        strokeWidth = 3f
    )
    // Inferior
    drawLine(
        color = NeonPurple,
        start = Offset(x, y + h),
        end = Offset(x + width, y + h),
        strokeWidth = 2f
    )

    // 5. "Pernos" o Luces en las esquinas
    drawCircle(Color.White, 3f, Offset(x + 5f, y + h/2))
    drawCircle(Color.White, 3f, Offset(x + width - 5f, y + h/2))
}

// 4. META: Bandera de Energía (Beacon)
fun DrawScope.drawFinishFlag(x: Float, y: Float) {
    // Asta con luz pulsante
    drawRect(
        brush = Brush.verticalGradient(listOf(Color.White, Color.Gray)),
        topLeft = Offset(x, y - 180f),
        size = Size(6f, 180f)
    )

    // Bandera con diseño de "alas"
    val flagPath = Path().apply {
        moveTo(x + 6f, y - 180f)
        lineTo(x + 80f, y - 140f)
        lineTo(x + 20f, y - 130f)
        lineTo(x + 80f, y - 120f)
        lineTo(x + 6f, y - 100f)
        close()
    }

    drawPath(
        brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
        path = flagPath
    )

    // Borde brillante de la bandera
    drawPath(flagPath, color = Color.White, style = Stroke(width = 2f))
}
