package com.ngocdevv.onboardingtags.physics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextLayoutResult
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

// Physics / layout configuration (mirrors PhysicsWorld.tsx constants), in dp.
const val TAG_HEIGHT_DP = 44f
const val TAG_RADIUS_DP = 22f
const val GAP_DP = 2f
// Layout: [padding 12] [icon 16] [gap 4] [label] [padding 16]
const val ICON_AREA_DP = 32f
const val H_PADDING_DP = 16f
const val MIN_TAG_WIDTH_DP = 80f
const val ICON_SIZE_DP = 16f

// matter-js tuning converted to Box2D units (1 m = 100 dp).
//   gravity.y = 0.5  →  500 dp/s²  →  5 m/s²
//   frictionAir 0.02 per 60 Hz step  →  linear damping ≈ 1.2 /s
//   applyForce {y: -0.05}  →  ≈ 190 dp/s upward kick  →  1.9 m/s
private const val GRAVITY = 5f
private const val AIR_DAMPING = 1.2f
private const val TAP_KICK_VELOCITY = 1.9f
private const val STEP_SECONDS = 1f / 60f

/** One falling tag: physics body plus the precomputed label layout. */
class TagBody(
    val tag: Tag,
    val body: Body,
    val widthPx: Float,
    val label: TextLayoutResult,
)

/**
 * JBox2D replacement for the matter-js engine in `PhysicsWorld.tsx`.
 * All public geometry is in pixels; conversion to meters happens here.
 */
class PhysicsEngine(
    widthPx: Float,
    heightPx: Float,
    private val density: Float,
    measureLabel: (String) -> TextLayoutResult,
) {
    private val ppm = 100f * density // pixels per meter
    private val world = World(Vec2(0f, GRAVITY))

    val tags: List<TagBody>

    var selectedTagIds by mutableStateOf(emptySet<String>())
        private set

    init {
        val width = widthPx / ppm
        val height = heightPx / ppm
        val dp = density / ppm // dp → meters

        // Ground + walls based on container dimensions
        val wallThickness = 100f * dp
        addStatic(width / 2, height + 50f * dp, width, 100f * dp)
        addStatic(-wallThickness / 2, height / 2, wallThickness, height * 2)
        addStatic(width + wallThickness / 2, height / 2, wallThickness, height * 2)

        tags = TAGS.mapIndexed { index, tag ->
            val label = measureLabel(tag.label)
            // Dynamic width based on label text
            val textWidthDp = label.size.width / density
            val tagWidthDp = max(MIN_TAG_WIDTH_DP, ICON_AREA_DP + textWidthDp + H_PADDING_DP)
            val tagWidth = tagWidthDp * dp

            val x = Random.nextFloat() * max(0f, width - tagWidth) + tagWidth / 2
            val y = -(100f + index * 60f) * dp // start falling from above
            val body = createTagBody(
                x = x,
                y = y,
                width = (tagWidthDp + GAP_DP) * dp,
                height = (TAG_HEIGHT_DP + GAP_DP) * dp,
                cut = TAG_RADIUS_DP * 0.6f * dp,
                angle = (Random.nextFloat() - 0.5f) * 0.5f,
            )
            TagBody(tag, body, tagWidthDp * density, label)
        }
    }

    private fun addStatic(cx: Float, cy: Float, width: Float, height: Float) {
        val def = BodyDef().apply {
            type = BodyType.STATIC
            position.set(cx, cy)
        }
        val shape = PolygonShape().apply { setAsBox(width / 2, height / 2) }
        world.createBody(def).createFixture(FixtureDef().apply {
            this.shape = shape
            friction = 0.5f
        })
    }

    private fun createTagBody(
        x: Float, y: Float, width: Float, height: Float, cut: Float, angle: Float,
    ): Body {
        val def = BodyDef().apply {
            type = BodyType.DYNAMIC
            position.set(x, y)
            this.angle = angle
            linearDamping = AIR_DAMPING
            angularDamping = AIR_DAMPING
        }
        val body = world.createBody(def)

        // Convex octagon approximating `chamfer: { radius }` (counter-clockwise).
        val hx = width / 2
        val hy = height / 2
        val c = min(cut, min(hx, hy))
        val vertices = arrayOf(
            Vec2(hx - c, -hy),
            Vec2(hx, -hy + c),
            Vec2(hx, hy - c),
            Vec2(hx - c, hy),
            Vec2(-hx + c, hy),
            Vec2(-hx, hy - c),
            Vec2(-hx, -hy + c),
            Vec2(-hx + c, -hy),
        )
        val shape = PolygonShape().apply { set(vertices, vertices.size) }
        body.createFixture(FixtureDef().apply {
            this.shape = shape
            this.density = 1f
            friction = 0.5f
            restitution = 0.6f
        })
        return body
    }

    /** Advances the simulation by one fixed 60 Hz step (`Matter.Engine.update`). */
    fun step() {
        world.step(STEP_SECONDS, 6, 2)
    }

    /** `Matter.Query.point` + selection toggle + upward kick, in pixel coordinates. */
    fun tap(xPx: Float, yPx: Float) {
        val point = Vec2(xPx / ppm, yPx / ppm)
        val hit = tags.firstOrNull { it.containsPoint(point) } ?: return

        val id = hit.tag.id
        selectedTagIds = if (id in selectedTagIds) selectedTagIds - id else selectedTagIds + id

        val body = hit.body
        body.applyLinearImpulse(Vec2(0f, -body.mass * TAP_KICK_VELOCITY), body.worldCenter)
    }

    fun toPx(meters: Float): Float = meters * ppm

    private fun TagBody.containsPoint(point: Vec2): Boolean {
        var fixture = body.fixtureList
        while (fixture != null) {
            if (fixture.testPoint(point)) return true
            fixture = fixture.next
        }
        return false
    }
}
