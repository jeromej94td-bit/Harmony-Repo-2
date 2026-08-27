package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText

private data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val width: Float = 11f
)

@Composable
fun DrawingPromptCanvas(
    prompt: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strokes = remember(prompt) { mutableStateListOf<DrawingStroke>() }
    var selectedColor by remember(prompt) { mutableStateOf(Color(0xFF33263E)) }
    val palette = remember {
        listOf(
            Color(0xFF33263E),
            Color(0xFFE74485),
            Color(0xFF8B5CFF),
            Color(0xFF2B7FFF),
            Color(0xFF23A96E),
            Color(0xFFFFA928)
        )
    }

    Column(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prompt,
            color = HarmonyText,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFFFBFF), RoundedCornerShape(22.dp))
                .border(2.dp, HarmonyLine, RoundedCornerShape(22.dp))
                .padding(5.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(prompt, selectedColor) {
                        detectDragGestures(
                            onDragStart = { point ->
                                strokes.add(DrawingStroke(points = listOf(point), color = selectedColor))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val last = strokes.lastOrNull() ?: return@detectDragGestures
                                strokes[strokes.lastIndex] = last.copy(points = last.points + change.position)
                            }
                        )
                    }
            ) {
                strokes.forEach { stroke ->
                    stroke.points.zipWithNext().forEach { (from, to) ->
                        drawLine(
                            color = stroke.color,
                            start = from,
                            end = to,
                            strokeWidth = stroke.width,
                            cap = StrokeCap.Round
                        )
                    }
                    if (stroke.points.size == 1) {
                        drawCircle(stroke.color, radius = stroke.width / 2f, center = stroke.points.first())
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Farben", color = HarmonyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            palette.forEach { color ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selectedColor == color) 34.dp else 29.dp)
                        .background(color, CircleShape)
                        .border(if (selectedColor == color) 3.dp else 1.dp, Color.White, CircleShape)
                        .clickable { selectedColor = color }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                TextButton(onClick = { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) }) {
                    Text("↶ Rückgängig", color = HarmonyText)
                }
                TextButton(onClick = { strokes.clear() }) {
                    Text("Leeren", color = HarmonyText)
                }
            }
            Button(
                onClick = onDone,
                enabled = strokes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurple, disabledContainerColor = HarmonySurface2),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Fertig 🎨", fontWeight = FontWeight.Bold)
            }
        }
    }
}
