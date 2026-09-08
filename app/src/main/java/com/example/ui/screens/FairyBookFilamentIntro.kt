package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.android.filament.View
import io.github.sceneview.RenderQuality
import io.github.sceneview.SceneView
import io.github.sceneview.SurfaceType
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberView
import kotlinx.coroutines.delay

private const val FAIRY_BOOK_MODEL = "models/harmony_magic_book.glb"
private const val FAIRY_BOOK_CINEMATIC_MS = 3_650L
private const val FAIRY_BOOK_LOAD_TIMEOUT_MS = 1_600L

@Composable
fun FairyBookFilamentIntro(
    onFinished: () -> Unit,
    onUnavailable: () -> Unit,
    modifier: Modifier = Modifier
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val modelInstance = rememberModelInstance(modelLoader, FAIRY_BOOK_MODEL)
    val filamentView = rememberView(engine)

    LaunchedEffect(filamentView) {
        filamentView.bloomOptions = filamentView.bloomOptions.apply {
            enabled = true
            strength = 0.34f
            dirtStrength = 0.18f
        }
        filamentView.ambientOcclusionOptions =
            filamentView.ambientOcclusionOptions.apply {
                enabled = true
                quality = View.QualityLevel.HIGH
                upsampling = View.QualityLevel.HIGH
            }
    }

    LaunchedEffect(modelInstance) {
        if (modelInstance == null) {
            delay(FAIRY_BOOK_LOAD_TIMEOUT_MS)
            onUnavailable()
        } else {
            delay(FAIRY_BOOK_CINEMATIC_MS)
            onFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF5E1B8C),
                        Color(0xFF1A092C),
                        Color(0xFF05020B)
                    ),
                    radius = 1_650f
                )
            )
    ) {
        SceneView(
            modifier = Modifier.fillMaxSize(),
            surfaceType = SurfaceType.TextureSurface,
            engine = engine,
            modelLoader = modelLoader,
            view = filamentView,
            isOpaque = false,
            renderQuality = RenderQuality.Cinematic,
            autoCenterContent = true,
            autoFitContent = true,
            cameraManipulator = null
        ) {
            modelInstance?.let { instance ->
                ModelNode(
                    modelInstance = instance,
                    autoAnimate = true,
                    scaleToUnits = 1.55f
                )
            }
        }
    }
}
