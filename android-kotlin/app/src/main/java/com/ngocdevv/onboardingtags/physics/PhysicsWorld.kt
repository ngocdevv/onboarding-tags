package com.ngocdevv.onboardingtags.physics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngocdevv.onboardingtags.ui.components.Entering
import com.ngocdevv.onboardingtags.ui.components.entering
import kotlinx.coroutines.isActive

private const val STEP_SECONDS = 1.0 / 60.0
private const val MAX_FRAME_SECONDS = 0.1 // cap catch-up after long pauses

/**
 * Port of `src/components/PhysicsWorld.tsx`.
 *
 * The original drives matter-js and paints every body with Skia each frame.
 * Here [PhysicsEngine] (JBox2D) runs the simulation on `withFrameNanos`, a
 * Compose `Canvas` draws the bodies, and the progress bar + header are
 * layered on top.
 */
@Composable
fun PhysicsWorld(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = remember {
        TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            fontFamily = FontFamily.SansSerif,
        )
    }
    val iconPainters = TAGS.associate { it.id to rememberVectorPainter(it.icon) }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val engine = remember(canvasSize) {
        if (canvasSize.width == 0 || canvasSize.height == 0) {
            null
        } else {
            PhysicsEngine(
                widthPx = canvasSize.width.toFloat(),
                heightPx = canvasSize.height.toFloat(),
                density = density.density,
                measureLabel = { textMeasurer.measure(it, labelStyle) },
            )
        }
    }

    // requestAnimationFrame loop → Matter.Engine.update + re-render.
    // Steps are fixed at 60 Hz but driven by wall-clock time, so the
    // simulation keeps real-time speed even when the display drops frames.
    var frame by remember { mutableLongStateOf(0L) }
    LaunchedEffect(engine) {
        if (engine == null) return@LaunchedEffect
        var lastNanos = withFrameNanos { it }
        var accumulator = 0.0
        while (isActive) {
            val now = withFrameNanos { it }
            accumulator += (now - lastNanos) / 1_000_000_000.0
            lastNanos = now
            if (accumulator > MAX_FRAME_SECONDS) accumulator = MAX_FRAME_SECONDS
            while (accumulator >= STEP_SECONDS) {
                engine.step()
                accumulator -= STEP_SECONDS
            }
            frame++
        }
    }

    Box(modifier) {
        Canvas(
            Modifier
                .fillMaxSize()
                .extendBottom(12.dp) // Canvas style: absoluteFill + bottom: -12
                .onSizeChanged { canvasSize = it }
                .pointerInput(engine) {
                    detectTapGestures { offset -> engine?.tap(offset.x, offset.y) }
                },
        ) {
            @Suppress("UNUSED_VARIABLE") val currentFrame = frame // subscribe to the frame loop
            val world = engine ?: return@Canvas

            val tagHeight = TAG_HEIGHT_DP.dp.toPx()
            val radius = CornerRadius(TAG_RADIUS_DP.dp.toPx())
            val iconSize = ICON_SIZE_DP.dp.toPx()
            val iconInset = 12.dp.toPx()
            val labelInset = ICON_AREA_DP.dp.toPx()
            val strokeWidth = 2.dp.toPx()

            for (tagBody in world.tags) {
                val cx = world.toPx(tagBody.body.position.x)
                val cy = world.toPx(tagBody.body.position.y)
                val degrees = Math.toDegrees(tagBody.body.angle.toDouble()).toFloat()
                val tagWidth = tagBody.widthPx
                val leftEdge = cx - tagWidth / 2
                val top = cy - tagHeight / 2
                val isSelected = tagBody.tag.id in world.selectedTagIds

                rotate(degrees, pivot = Offset(cx, cy)) {
                    drawRoundRect(
                        color = tagBody.tag.color,
                        topLeft = Offset(leftEdge, top),
                        size = Size(tagWidth, tagHeight),
                        cornerRadius = radius,
                    )
                    if (isSelected) {
                        drawRoundRect(
                            color = Color.Black,
                            topLeft = Offset(leftEdge, top),
                            size = Size(tagWidth, tagHeight),
                            cornerRadius = radius,
                            style = Stroke(width = strokeWidth),
                        )
                    }

                    // Icon (Material icon in place of the MaterialCommunityIcons glyph)
                    iconPainters[tagBody.tag.id]?.let { painter ->
                        translate(left = leftEdge + iconInset, top = cy - iconSize / 2) {
                            with(painter) {
                                draw(
                                    size = Size(iconSize, iconSize),
                                    colorFilter = ColorFilter.tint(Color(0xFF555555)),
                                )
                            }
                        }
                    }

                    drawText(
                        textLayoutResult = tagBody.label,
                        topLeft = Offset(leftEdge + labelInset, cy - tagBody.label.size.height / 2f),
                    )
                }
            }
        }

        // Content Overlay (pointerEvents="box-none")
        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 10.dp),
        ) {
            // Progress Bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .entering(Entering.FadeInUp, delayMillis = 300),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(5) { index ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                if (index == 0) Color(0xFF1A1A1A) else Color(0xFFF0F0F0),
                                RoundedCornerShape(3.dp),
                            ),
                    )
                }
            }

            // Header
            Column(
                Modifier.padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Build Around\nWhat You Love 💡",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 34.sp,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.entering(Entering.FadeInDown, delayMillis = 500),
                )
                Text(
                    text = "Choosing activities helps you track\nprogress and stay motivated every day",
                    fontSize = 15.sp,
                    color = Color(0xFF666666),
                    lineHeight = 22.sp,
                    modifier = Modifier.entering(Entering.FadeInDown, delayMillis = 600),
                )
            }
        }
    }
}

/** Lets the canvas overflow its parent by [extra] at the bottom (RN `bottom: -12`). */
private fun Modifier.extendBottom(extra: Dp): Modifier = layout { measurable, constraints ->
    val extraPx = extra.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(
            minHeight = constraints.maxHeight + extraPx,
            maxHeight = constraints.maxHeight + extraPx,
        ),
    )
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(0, 0)
    }
}
