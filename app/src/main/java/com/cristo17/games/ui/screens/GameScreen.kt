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
    val context = LocalContext.current

    // --- REPRODUCTORES DE MÚSICA ---
    // Música Nivel 2
    val playerLevel2 = remember {
        try {
            MediaPlayer.create(context, R.raw.pleasant_porridge).apply {
                isLooping = true
                setVolume(0.6f, 0.6f)
            }
        } catch (_: Exception) { null }
    }

    // Música Nivel 3
    val playerLevel3 = remember {
        try {
            MediaPlayer.create(context, R.raw.intrumental).apply {
                isLooping = true
                setVolume(0.6f, 0.6f)
            }
        } catch (_: Exception) { null }
    }

    DisposableEffect(Unit) {
        onDispose {
            playerLevel2?.release()
            playerLevel3?.release()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val starDrift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 5000f,
        animationSpec = infiniteRepeatable(tween(120000, easing = LinearEasing), RepeatMode.Restart),
        label = "starDrift"
    )
    val nebulaPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(5000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "nebulaPulse"
    )

    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isLevelWon by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var scoreMeters by remember { mutableIntStateOf(0) }

    var playerX by remember { mutableFloatStateOf(0f) }
    var playerY by remember { mutableFloatStateOf(900f) }
    var playerVelocityY by remember { mutableFloatStateOf(0f) }
    var moveDirection by remember { mutableIntStateOf(0) }
    var cameraY by remember { mutableFloatStateOf(0f) }
    var platforms by remember { mutableStateOf(listOf<Platform>()) }

    val gravity = 1.3f
    val jumpForce = -38f
    val playerSize = 60f
    val lateralSpeed = 12f

    // Metas por nivel
    val levelMetas = mapOf(1 to 50, 2 to 80, 3 to 120)

    // --- LÓGICA DE MÚSICA POR NIVEL ---
    LaunchedEffect(isPlaying, isPaused, isGameOver, isLevelWon, selectedLevel) {
        // Detener todas primero para resetear
        playerLevel2?.pause()
        playerLevel3?.pause()

        if (isPlaying && !isPaused && !isGameOver && !isLevelWon) {
            when (selectedLevel?.id) {
                2 -> playerLevel2?.start()
                3 -> playerLevel3?.start()
            }
        }
    }

    val configuration = LocalConfiguration.current
    val sWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val sHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

    fun startLevel(level: Level) {
        selectedLevel = level
        playerX = sWidth / 2 - playerSize / 2
        playerY = 900f

        val metaMeters = levelMetas[level.id] ?: 100
        val platformCount = metaMeters / 5

        val levelPlatforms = mutableListOf<Platform>()
        var currentY = 1000f

        for (i in 0 until platformCount) {
            currentY -= level.verticalGap
            val design = level.layout[i % level.layout.size]
            val hasSpike = if (i == platformCount - 1) false else design.second
            levelPlatforms.add(Platform(x = design.first, y = currentY, width = level.platformWidth, hasSpike = hasSpike))
        }

        platforms = listOf(Platform(playerX - 100f, 1000f, 300f)) + levelPlatforms
        playerVelocityY = jumpForce
        cameraY = 0f
        scoreMeters = 0
        isGameOver = false
        isLevelWon = false
        isPaused = false
        isPlaying = true
    }

    val currentLevel = selectedLevel ?: gameLevels[0]

    LaunchedEffect(isPlaying, isGameOver, isPaused, isLevelWon, moveDirection) {
        if (isPlaying && !isGameOver && !isPaused && !isLevelWon) {
            while (true) {
                playerVelocityY += gravity
                playerY += playerVelocityY
                playerX += moveDirection * lateralSpeed
                playerX = playerX.coerceIn(0f, sWidth - playerSize)
                val targetCameraY = playerY - (sHeight * 0.6f)
                if (targetCameraY < cameraY) cameraY += (targetCameraY - cameraY) * 0.15f

                if (playerVelocityY > 0) {
                    platforms.forEachIndexed { index, p ->
                        if (playerY + playerSize > p.y && playerY + playerSize < p.y + 45f &&
                            playerX + playerSize > p.x && playerX < p.x + p.width) {
                            playerVelocityY = jumpForce
                            val m = (index * 5)
                            scoreMeters = maxOf(scoreMeters, m)
                            if (index == platforms.size - 1) {
                                isLevelWon = true
                                isPlaying = false
                            }
                        }
                    }
                }

                platforms.filter { it.hasSpike }.forEach { p ->
                    val spikeX = p.x + (p.width / 2) - 25f
                    if (playerX + playerSize > spikeX + 15f && playerX < spikeX + 35f &&
                        playerY + playerSize > p.y - 50f && playerY < p.y) {
                        isGameOver = true
                        isPlaying = false
                    }
                }

                if (playerY > cameraY + sHeight) {
                    isGameOver = true
                    isPlaying = false
                }
                delay(16)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onPress = { offset ->
            if (isPlaying && !isPaused) {
                moveDirection = if (offset.x < sWidth / 2) -1 else 1
                tryAwaitRelease()
                moveDirection = 0
            }
        })
    }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Brush.verticalGradient(listOf(currentLevel.backgroundColor, Color(0xFF010105))))
            drawCircle(
                brush = Brush.radialGradient(listOf(currentLevel.nebulaColor.copy(alpha = nebulaPulse), Color.Transparent)),
                center = Offset(size.width * 0.7f, size.height * 0.3f), radius = 1200f
            )
            for (i in 0..60) {
                val starX = (i * 137.5f + starDrift * (if(i % 2 == 0) 0.1f else 0.05f)) % size.width
                val starY = (i * 245.3f - cameraY * 0.3f) % size.height
                drawCircle(
                    color = Color.White.copy(alpha = if (i % 2 == 0) 0.7f else 0.3f),
                    radius = if (i % 5 == 0) 2f else 1f,
                    center = Offset(starX, if (starY < 0) starY + size.height else starY)
                )
            }

            val offset = -cameraY
            platforms.forEachIndexed { index, p ->
                val drawY = p.y + offset
                if (drawY in -300f..size.height + 400f) {
                    drawPlatform(p.x, drawY, p.width)
                    if (p.hasSpike) drawSpike(p.x + (p.width / 2) - 25f, drawY)
                    if (index == platforms.size - 1) {
                        drawFinishFlag(p.x + (p.width / 2) - 10f, drawY)
                    }
                }
            }
            drawPlayer(playerX, playerY + offset, playerSize)
        }

        if (isPlaying && !isGameOver && !isLevelWon) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(currentLevel.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    val meta = levelMetas[currentLevel.id] ?: 100
                    Text("$scoreMeters / ${meta}m", color = Color.Cyan, fontSize = 16.sp)
                }
                IconButton(onClick = { isPaused = true }, modifier = Modifier.background(Color.White.copy(0.15f), RoundedCornerShape(12.dp))) {
                    Icon(Icons.Default.Menu, null, tint = Color.White)
                }
            }
        }

        if (!isPlaying && !isGameOver && !isLevelWon && !isPaused) LevelSelector { startLevel(it) }

        if (isPaused) MenuOverlay("PAUSA", null, "CONTINUAR", "SALIR") { action ->
            if (action == "CONTINUAR") isPaused = false else isPlaying = false
        }

        if (isGameOver) MenuOverlay("¡HAS CAÍDO!", "Llegaste a los $scoreMeters m", "REINTENTAR", "MENÚ") { action ->
            if (action == "REINTENTAR") startLevel(currentLevel) else { isGameOver = false; isPlaying = false }
        }

        if (isLevelWon) MenuOverlay("¡NIVEL COMPLETADO!", "¡Llegaste a la bandera!", "SIGUIENTE NIVEL", "VOLVER AL MENÚ") { action ->
            if (action == "SIGUIENTE NIVEL" && currentLevel.id < gameLevels.size) startLevel(gameLevels[currentLevel.id])
            else { isLevelWon = false; isPlaying = false }
        }
    }
}

@Composable
fun LevelSelector(onLevelSelected: (Level) -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.92f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("GEOMETRY JUMP", color = Color.Cyan, fontSize = 38.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(40.dp))
            gameLevels.forEach { level ->
                Button(onClick = { onLevelSelected(level) }, modifier = Modifier.fillMaxWidth(0.75f).height(70.dp).padding(vertical = 4.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E))) {
                    Text("${level.name} (Meta: ${level.targetHeight}m)", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MenuOverlay(title: String, subtitle: String?, buttonText: String, secondaryButtonText: String?, onAction: (String) -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.88f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.Cyan, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            if (subtitle != null) Text(subtitle, color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(top = 12.dp))
            Spacer(Modifier.height(40.dp))
            Button(onClick = { onAction(buttonText) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan), modifier = Modifier.size(220.dp, 60.dp), shape = RoundedCornerShape(12.dp)) {
                Text(buttonText, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            if (secondaryButtonText != null) {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { onAction(secondaryButtonText) }, border = androidx.compose.foundation.BorderStroke(2.dp, Color.Cyan), modifier = Modifier.size(220.dp, 60.dp), shape = RoundedCornerShape(12.dp)) {
                    Text(secondaryButtonText, color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
