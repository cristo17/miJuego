# Geometry Jump: Aventura Espacial

Este es un juego de plataformas vertical infinito inspirado en la estética de *Geometry Dash* y la jugabilidad de *Doodle Jump*. El jugador controla un cubo que debe ascender lo más alto posible saltando sobre plataformas en un entorno espacial dinámico.

## Cómo Jugar

- **Movimiento**: Mantén pulsado el lado **izquierdo** o **derecho** de la pantalla para mover el cubo de forma fluida en esa dirección.
- **Salto**: El cubo salta **automáticamente** al tocar cualquier plataforma.
- **Objetivo**: Superar los 3 niveles de dificultad creciente, cada uno con una meta de altura y un diseño de plataformas único.
- **Peligros**: Evita tocar los **pinchos rojos** o caer al vacío.

## Estructura del Proyecto y Código

El código está organizado siguiendo las mejores prácticas de Android con Jetpack Compose:

### 1. `data/model/GameModels.kt`

Define las estructuras de datos (`Platform`, `Level`) y el diseño de los niveles en la lista `gameLevels`.

```kotlin
package com.cristo17.games.data.model

import androidx.compose.ui.graphics.Color

data class Platform(
    val x: Float,
    val y: Float,
    val width: Float = 180f,
    val hasSpike: Boolean = false
)

data class Level(
    val id: Int,
    val name: String,
    val platformWidth: Float,
    val verticalGap: Float,
    val backgroundColor: Color,
    val nebulaColor: Color,
    val targetHeight: Int, // Agregamos la meta directamente aquí
    val layout: List<Pair<Float, Boolean>> // Diseño de las plataformas
)

// DISEÑO DE NIVELES MANUAL - LARGOS
val gameLevels = listOf(
    Level(
        id = 1,
        name = "Nivel 1: Nebulosa Azul",
        platformWidth = 220f,
        verticalGap = 280f,
        backgroundColor = Color(0xFF00001E),
        nebulaColor = Color(0xFF003366),
        targetHeight = 50,
        layout = listOf(
            Pair(400f, false), Pair(200f, false), Pair(600f, true),
            Pair(300f, false), Pair(100f, false), Pair(500f, true),
            Pair(700f, false), Pair(400f, true), Pair(200f, false),
            Pair(500f, false) // Meta 50m
        )
    ),
    Level(
        id = 2,
        name = "Nivel 2: Cúmulo Escarlata",
        platformWidth = 160f,
        verticalGap = 320f,
        backgroundColor = Color(0xFF1E001E),
        nebulaColor = Color(0xFF660033),
        targetHeight = 80,
        layout = listOf(
            Pair(100f, false), Pair(400f, true), Pair(700f, false),
            Pair(400f, true), Pair(100f, false), Pair(300f, true),
            Pair(600f, false), Pair(800f, true), Pair(500f, false),
            Pair(200f, false), Pair(400f, true), Pair(700f, false),
            Pair(300f, true), Pair(100f, false), Pair(500f, true),
            Pair(400f, false) // Meta 80m
        )
    ),
    Level(
        id = 3,
        name = "Nivel 3: El Abismo Verde",
        platformWidth = 130f,
        verticalGap = 360f,
        backgroundColor = Color(0xFF001E00),
        nebulaColor = Color(0xFF006633),
        targetHeight = 120,
        layout = listOf(
            Pair(500f, false), Pair(200f, true), Pair(800f, true),
            Pair(400f, false), Pair(100f, true), Pair(600f, true),
            Pair(300f, false), Pair(700f, true), Pair(200f, true),
            Pair(500f, false), Pair(800f, true), Pair(400f, false),
            Pair(100f, true), Pair(600f, true), Pair(300f, false),
            Pair(700f, true), Pair(200f, false), Pair(500f, true),
            Pair(800f, true), Pair(400f, false), Pair(100f, true),
            Pair(600f, false), Pair(300f, true), Pair(500f, false) // Meta 120m
        )
    )
)
```

### 2. `ui/components/GameComponents.kt`

Contiene las funciones de dibujo con `DrawScope` para cada elemento visual del juego (jugador, plataformas, pinchos y bandera).

```kotlin
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

fun DrawScope.drawPlayer(x: Float, y: Float, size: Float) {
    drawRoundRect(
        color = NeonYellow.copy(alpha = 0.2f),
        topLeft = Offset(x - 5f, y - 5f),
        size = Size(size + 10f, size + 10f),
        cornerRadius = CornerRadius(12f, 12f)
    )
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
    drawRect(
        color = Color.Black.copy(alpha = 0.7f),
        topLeft = Offset(x + (size * 0.1f), y + (size * 0.25f)),
        size = Size(size * 0.8f, size * 0.25f)
    )
    drawLine(
        color = Color.White.copy(alpha = 0.6f),
        start = Offset(x + (size * 0.15f), y + (size * 0.3f)),
        end = Offset(x + (size * 0.4f), y + (size * 0.3f)),
        strokeWidth = 3f
    )
}

fun DrawScope.drawSpike(x: Float, y: Float) {
    val path = Path().apply {
        moveTo(x + 25f, y - 60f)
        lineTo(x + 50f, y)
        lineTo(x, y)
        close()
    }
    val spikeBrush = Brush.verticalGradient(
        colors = listOf(Color.White, NeonRed, Color(0xFF440011)),
        startY = y - 60f,
        endY = y
    )
    drawPath(path, brush = spikeBrush)
    drawPath(path, color = Color.White.copy(alpha = 0.4f), style = Stroke(width = 2f))
}

fun DrawScope.drawPlatform(x: Float, y: Float, width: Float) {
    val h = 20f
    drawRect(
        color = NeonCyan.copy(alpha = 0.15f),
        topLeft = Offset(x - 10f, y - 5f),
        size = Size(width + 20f, h + 10f)
    )
    drawRect(
        color = Color(0xFF111111),
        topLeft = Offset(x, y),
        size = Size(width, h)
    )
    val step = width / 6
    for (i in 0..6) {
        drawLine(
            color = NeonCyan.copy(alpha = 0.3f),
            start = Offset(x + (i * step), y),
            end = Offset(x + (i * step), y + h),
            strokeWidth = 1f
        )
    }
    drawLine(
        color = NeonCyan,
        start = Offset(x, y),
        end = Offset(x + width, y),
        strokeWidth = 3f
    )
    drawLine(
        color = NeonPurple,
        start = Offset(x, y + h),
        end = Offset(x + width, y + h),
        strokeWidth = 2f
    )
    drawCircle(Color.White, 3f, Offset(x + 5f, y + h/2))
    drawCircle(Color.White, 3f, Offset(x + width - 5f, y + h/2))
}

fun DrawScope.drawFinishFlag(x: Float, y: Float) {
    val poleGradient = Brush.verticalGradient(listOf(Color.White, Color.Gray))
    drawRect(
        brush = poleGradient,
        topLeft = Offset(x, y - 180f),
        size = Size(6f, 180f)
    )
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
    drawPath(flagPath, color = Color.White, style = Stroke(width = 2f))
}
```

### 3. `ui/screens/GameScreen.kt`

Es el corazón del juego. Contiene toda la lógica de estado, el bucle principal (`LaunchedEffect`) que gestiona la física y colisiones, y el `Canvas` donde se dibuja el juego.

```kotlin
package com.cristo17.games.ui.screens

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristo17.games.R
import com.cristo17.games.data.model.Level
import com.cristo17.games.data.model.Platform
import com.cristo17.games.data.model.gameLevels
import com.cristo17.games.ui.components.*
import kotlinx.coroutines.delay

@Composable
fun GameScreen() {
    // ... (Toda la lógica y el Canvas del juego)
}

@Composable
fun LevelSelector(onLevelSelected: (Level) -> Unit) {
    // ... (UI para el menú de selección de nivel)
}

@Composable
fun MenuOverlay(title: String, subtitle: String?, buttonText: String, secondaryButtonText: String?, onAction: (String) -> Unit) {
    // ... (UI para las pantallas de Pausa, Game Over y Victoria)
}
```

### 4. `MainActivity.kt`

El punto de entrada de la aplicación. Su única responsabilidad es lanzar la `GameScreen`.

```kotlin
package com.cristo17.games

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cristo17.games.ui.screens.GameScreen
import com.cristo17.games.ui.theme.GamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen()
                }
            }
        }
    }
}
```
