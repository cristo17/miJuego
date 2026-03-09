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
