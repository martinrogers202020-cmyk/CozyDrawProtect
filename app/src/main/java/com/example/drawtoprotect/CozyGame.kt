package com.cozyprotect

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val WORLD_WIDTH = 10f
private const val WORLD_HEIGHT = 16f

class CozyGame(private val level: LevelDefinition) : ApplicationAdapter() {
    private lateinit var world: World
    private lateinit var camera: OrthographicCamera
    private lateinit var renderer: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont

    private var mascotBody: Body? = null
    private val hazards = mutableListOf<Body>()
    private val drawnBodies = mutableListOf<Body>()
    private val drawPoints = mutableListOf<Vector2>()
    private var remainingDraws = level.drawLimit
    private var currentMaterial = level.allowedMaterials.firstOrNull() ?: MaterialType.RUBBER
    private var elapsed = 0f
    private var safeState = false
    private var worriedState = false
    private var sparkleTimer = 0f

    override fun create() {
        Box2D.init()
        world = World(Vector2(0f, -9.8f), true)
        camera = OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT).apply {
            position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0f)
            update()
        }
        renderer = ShapeRenderer()
        batch = SpriteBatch()
        font = BitmapFont()
        setupWorld()
        setupInput()
        setupContacts()
    }

    private fun setupWorld() {
        val groundDef = BodyDef().apply { position.set(0f, 1f) }
        val groundBody = world.createBody(groundDef)
        val groundShape = EdgeShape().apply { set(0f, 0f, WORLD_WIDTH, 0f) }
        groundBody.createFixture(groundShape, 0f)
        groundShape.dispose()

        val mascotDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(level.characterX, level.characterY)
        }
        val mascotShape = CircleShape().apply { radius = 0.5f }
        val mascotFixture = FixtureDef().apply {
            shape = mascotShape
            density = 1f
            friction = 0.6f
            restitution = 0.2f
        }
        mascotBody = world.createBody(mascotDef).apply { createFixture(mascotFixture) }
        mascotShape.dispose()

        level.hazards.forEach { hazard ->
            when (hazard.type) {
                HazardType.BEE -> hazards += createBee(hazard.positionX, hazard.positionY)
                HazardType.ROCK -> hazards += createRock(hazard.positionX, hazard.positionY)
                HazardType.WIND -> hazards += createWindGust(hazard.positionX, hazard.positionY)
                HazardType.HOT_SOUP -> hazards += createHotSoup(hazard.positionX, hazard.positionY)
            }
        }
    }

    private fun setupInput() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val worldPoint = screenToWorld(screenX, screenY)
                if (handleMaterialTap(screenX, screenY)) return true
                if (remainingDraws <= 0) return false
                drawPoints.clear()
                drawPoints.add(worldPoint)
                return true
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                if (remainingDraws <= 0) return false
                val worldPoint = screenToWorld(screenX, screenY)
                if (drawPoints.lastOrNull()?.dst(worldPoint) ?: 1f > 0.2f) {
                    drawPoints.add(worldPoint)
                }
                return true
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (remainingDraws <= 0) return false
                if (drawPoints.size > 1) {
                    createDrawnLine(drawPoints.toList())
                    remainingDraws -= 1
                }
                drawPoints.clear()
                return true
            }
        }
    }

    private fun setupContacts() {
        world.setContactListener(object : ContactListener {
            override fun beginContact(contact: Contact) {
                val a = contact.fixtureA.body
                val b = contact.fixtureB.body
                handleHotSoupContact(a, b)
                handleHotSoupContact(b, a)
            }

            override fun endContact(contact: Contact) = Unit
            override fun preSolve(contact: Contact, oldManifold: com.badlogic.gdx.physics.box2d.Manifold) = Unit
            override fun postSolve(contact: Contact, impulse: com.badlogic.gdx.physics.box2d.ContactImpulse) = Unit
        })
    }

    private fun handleHotSoupContact(soup: Body, other: Body) {
        if (soup.userData == "HOT_SOUP" && other.userData is DrawnLineData) {
            val data = other.userData as DrawnLineData
            if (data.material == MaterialType.ICE) {
                data.durability -= 0.4f
            }
        }
    }

    private fun createBee(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
        }
        val shape = CircleShape().apply { radius = 0.3f }
        val fixture = FixtureDef().apply {
            this.shape = shape
            density = 0.3f
            friction = 0.2f
            restitution = 0.6f
        }
        val body = world.createBody(bodyDef)
        body.createFixture(fixture)
        shape.dispose()
        body.userData = "BEE"
        return body
    }

    private fun createRock(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
        }
        val shape = PolygonShape().apply { setAsBox(0.4f, 0.4f) }
        val fixture = FixtureDef().apply {
            this.shape = shape
            density = 1.2f
            friction = 0.8f
            restitution = 0.1f
        }
        val body = world.createBody(bodyDef)
        body.createFixture(fixture)
        shape.dispose()
        body.userData = "ROCK"
        return body
    }

    private fun createWindGust(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.KinematicBody
            position.set(x, y)
        }
        val shape = PolygonShape().apply { setAsBox(0.8f, 0.2f) }
        val fixture = FixtureDef().apply {
            this.shape = shape
            isSensor = true
        }
        val body = world.createBody(bodyDef)
        body.createFixture(fixture)
        shape.dispose()
        body.userData = "WIND"
        return body
    }

    private fun createHotSoup(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.KinematicBody
            position.set(x, y)
        }
        val shape = PolygonShape().apply { setAsBox(1.2f, 0.4f) }
        val fixture = FixtureDef().apply {
            this.shape = shape
            isSensor = true
        }
        val body = world.createBody(bodyDef)
        body.createFixture(fixture)
        shape.dispose()
        body.userData = "HOT_SOUP"
        return body
    }

    private fun createDrawnLine(points: List<Vector2>) {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(0f, 0f)
        }
        val body = world.createBody(bodyDef)
        val shape = EdgeShape()
        val material = MaterialProfile.from(currentMaterial)
        for (i in 0 until points.size - 1) {
            shape.set(points[i], points[i + 1])
            val fixture = FixtureDef().apply {
                this.shape = shape
                density = material.density
                friction = material.friction
                restitution = material.restitution
            }
            body.createFixture(fixture)
        }
        shape.dispose()
        body.userData = DrawnLineData(material = currentMaterial, durability = material.durability)
        drawnBodies += body
    }

    private fun screenToWorld(screenX: Int, screenY: Int): Vector2 {
        val x = screenX.toFloat() / Gdx.graphics.width * WORLD_WIDTH
        val y = (Gdx.graphics.height - screenY).toFloat() / Gdx.graphics.height * WORLD_HEIGHT
        return Vector2(x, y)
    }

    override fun render() {
        updateState()
        Gdx.gl.glClearColor(1f, 0.96f, 0.91f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        drawBackground()
        drawHazards()
        drawMascot()
        drawDrawnLines()
        drawHud()
        renderer.end()

        batch.projectionMatrix = camera.combined
        batch.begin()
        font.color = Color(0.43f, 0.3f, 0.22f, 1f)
        font.draw(batch, "Survive: ${max(0, level.timeToSurvive - elapsed.toInt())}s", 0.4f, WORLD_HEIGHT - 0.6f)
        font.draw(batch, "Draws: $remainingDraws", 0.4f, WORLD_HEIGHT - 1.3f)
        batch.end()

        world.step(Gdx.graphics.deltaTime, 6, 2)
    }

    private fun updateState() {
        val delta = Gdx.graphics.deltaTime
        elapsed += delta
        safeState = elapsed >= level.timeToSurvive
        worriedState = hazards.any { hazard ->
            mascotBody?.position?.dst(hazard.position) ?: 0f < 2.2f
        }
        hazards.forEach { hazard ->
            when (hazard.userData) {
                "BEE" -> hazard.applyForceToCenter(Vector2(0.4f, 0f), true)
                "WIND" -> hazard.setTransform(hazard.position.x + 0.01f, hazard.position.y, 0f)
                "HOT_SOUP" -> hazard.setTransform(hazard.position.x, hazard.position.y + 0.002f, 0f)
            }
        }

        val iterator = drawnBodies.iterator()
        while (iterator.hasNext()) {
            val body = iterator.next()
            val data = body.userData as? DrawnLineData ?: continue
            if (data.material == MaterialType.ICE) {
                data.durability -= delta * 0.02f
            }
            if (data.durability <= 0f) {
                world.destroyBody(body)
                iterator.remove()
            }
        }

        if (safeState) {
            sparkleTimer += delta
            // TODO: Play \"cute squeak\" SFX when the stage is cleared.
        } else {
            sparkleTimer = 0f
        }
    }

    private fun drawBackground() {
        renderer.color = Color(1f, 0.94f, 0.9f, 1f)
        renderer.rect(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
        renderer.color = Color(0.82f, 0.96f, 0.91f, 1f)
        renderer.rect(0f, 0f, WORLD_WIDTH, 2f)
    }

    private fun drawMascot() {
        val body = mascotBody ?: return
        // TODO: Swap in Mochi sprite sheet + breathing/blink animations.
        val baseColor = if (worriedState) Color(0.96f, 0.78f, 0.78f, 1f) else Color(0.98f, 0.86f, 0.9f, 1f)
        renderer.color = baseColor
        val pulse = if (safeState) 0.05f else 0.02f
        val radius = 0.5f + pulse * kotlin.math.sin(elapsed * 3f)
        renderer.circle(body.position.x, body.position.y, radius, 24)
        renderer.color = Color(0.36f, 0.22f, 0.18f, 1f)
        renderer.circle(body.position.x - 0.15f, body.position.y + 0.1f, 0.05f, 12)
        renderer.circle(body.position.x + 0.15f, body.position.y + 0.1f, 0.05f, 12)
        if (worriedState) {
            renderer.rect(body.position.x - 0.2f, body.position.y - 0.05f, 0.4f, 0.05f)
        } else {
            renderer.arc(body.position.x, body.position.y - 0.05f, 0.18f, 200f, 140f)
        }
        if (safeState && sparkleTimer > 0.3f) {
            renderer.color = Color(1f, 0.95f, 0.7f, 1f)
            renderer.circle(body.position.x + 0.6f, body.position.y + 0.5f, 0.08f, 10)
        }
    }

    private fun drawHazards() {
        hazards.forEach { body ->
            when (body.userData) {
                "BEE" -> {
                    renderer.color = Color(1f, 0.9f, 0.5f, 1f)
                    renderer.circle(body.position.x, body.position.y, 0.3f, 20)
                }
                "ROCK" -> {
                    renderer.color = Color(0.6f, 0.55f, 0.5f, 1f)
                    renderer.rect(body.position.x - 0.4f, body.position.y - 0.4f, 0.8f, 0.8f)
                }
                "WIND" -> {
                    renderer.color = Color(0.8f, 0.9f, 1f, 0.4f)
                    renderer.rect(body.position.x - 0.8f, body.position.y - 0.2f, 1.6f, 0.4f)
                }
                "HOT_SOUP" -> {
                    renderer.color = Color(1f, 0.7f, 0.6f, 0.6f)
                    renderer.rect(body.position.x - 1.2f, body.position.y - 0.4f, 2.4f, 0.8f)
                }
            }
        }
    }

    private fun drawDrawnLines() {
        drawnBodies.forEach { body ->
            val data = body.userData as? DrawnLineData ?: return@forEach
            renderer.color = data.material.uiColor
            body.fixtureList.forEach { fixture ->
                val edge = fixture.shape as EdgeShape
                val v1 = Vector2()
                val v2 = Vector2()
                edge.getVertex1(v1)
                edge.getVertex2(v2)
                renderer.rectLine(v1, v2, 0.15f)
            }
        }
        if (drawPoints.size > 1) {
            renderer.color = currentMaterial.uiColor.cpy().apply { a = 0.7f }
            for (i in 0 until drawPoints.size - 1) {
                renderer.rectLine(drawPoints[i], drawPoints[i + 1], 0.1f)
            }
        }
        // TODO: Trigger a \"soft pop\" SFX when the draw line is placed.
    }

    private fun drawHud() {
        if (level.allowedMaterials.isEmpty()) return

        val buttonWidth = WORLD_WIDTH / level.allowedMaterials.size.toFloat()
        val y = WORLD_HEIGHT - 1.2f

        level.allowedMaterials.forEachIndexed { index, material ->
            val x = index * buttonWidth

            val bg = Color(0.2f, 0.2f, 0.2f, 0.2f)
            val matColor = if (material == currentMaterial) {
                material.uiColor
            } else {
                material.uiColor.cpy().apply { a = 0.4f }
            }

            // Background strip (ShapeRenderer uses positional args, not named args)
            renderer.rect(
                x + 0.1f,
                y,
                buttonWidth - 0.2f,
                1f,
                bg, bg, bg, bg
            )

            // Material indicator dot
            renderer.color = matColor
            renderer.circle(
                x + buttonWidth / 2f,
                y + 0.5f,
                0.15f,
                12
            )

            renderer.color = Color.WHITE
        }
    }



    private fun handleMaterialTap(screenX: Int, screenY: Int): Boolean {
        val y = screenY.toFloat() / Gdx.graphics.height * WORLD_HEIGHT
        if (y < WORLD_HEIGHT - 1.2f) return false
        val index = (screenX.toFloat() / Gdx.graphics.width * level.allowedMaterials.size).toInt()
        val material = level.allowedMaterials.getOrNull(index) ?: return false
        currentMaterial = material
        // TODO: Play a \"gentle whoosh\" SFX on material selection.
        return true
    }

    override fun dispose() {
        renderer.dispose()
        batch.dispose()
        font.dispose()
        world.dispose()
    }
}

data class DrawnLineData(var material: MaterialType, var durability: Float)

data class MaterialProfile(
    val density: Float,
    val friction: Float,
    val restitution: Float,
    val durability: Float
) {
    companion object {
        fun from(material: MaterialType): MaterialProfile {
            return when (material) {
                MaterialType.RUBBER -> MaterialProfile(density = 0.4f, friction = 0.4f, restitution = 0.8f, durability = 1.2f)
                MaterialType.STONE -> MaterialProfile(density = 2.2f, friction = 0.9f, restitution = 0.1f, durability = 2.4f)
                MaterialType.ICE -> MaterialProfile(density = 0.7f, friction = 0.1f, restitution = 0.05f, durability = 0.6f)
                MaterialType.CLOTH -> MaterialProfile(density = 0.5f, friction = 0.6f, restitution = 0.2f, durability = 1.0f)
            }
        }
    }
}

val MaterialType.uiColor: Color
    get() = when (this) {
        MaterialType.RUBBER -> Color(0.96f, 0.78f, 0.88f, 1f)
        MaterialType.STONE -> Color(0.7f, 0.65f, 0.6f, 1f)
        MaterialType.ICE -> Color(0.7f, 0.9f, 1f, 1f)
        MaterialType.CLOTH -> Color(0.9f, 0.8f, 0.6f, 1f)
    }
