import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korau.sound.Sound
import com.soywiz.korau.sound.readSound
import com.soywiz.korge.*
import com.soywiz.korge.bus.GlobalBus
import com.soywiz.korge.input.onClick
import com.soywiz.korge.time.delay
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.BlurFilter
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.effect.BitmapEffect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.font.toBitmapFont
import com.soywiz.korim.format.*
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing
import com.soywiz.korma.random.get
import kotlin.random.Random

val gameWidth = 540
val gameHeight = 920

var apples = 5

val bus = GlobalBus()

val font = DefaultTtfFont.toBitmapFont(
	fontSize = 64.0, effect = BitmapEffect(dropShadowX = 2, dropShadowY = 2, dropShadowRadius = 1)
)

suspend fun main() = Korge(width = gameWidth, height = gameHeight, bgcolor = Colors["#2b2b2b"]) {

	val backgroundBitmap = resourcesVfs["background.png"].readBitmap()
	val treeBitmap = resourcesVfs["tree.png"].readBitmap()
	val appleBitmap = resourcesVfs["apple.png"].readBitmap()
	val birdBitmap = resourcesVfs["bird.png"].readBitmap()
	val eatApple = resourcesVfs["apple_eat.mp3"].readSound()
	val birdSound = resourcesVfs["bird_rip.mp3"].readSound()

	image(backgroundBitmap) {
		filter = BlurFilter(2.5)
		size(gameWidth, gameHeight)
	}

	container {
		name = "Happy Sun"
		circle(radius = 50.0, fill = Colors.YELLOW)
		circle(radius = 8.0, fill = Colors.BLACK).position(60, 20)
		circle(radius = 8.0, fill = Colors.BLACK).position(24, 20)
		circle(radius = 10.0, fill = Colors.RED).position(50, 60)
		position(50, 50)
	}

	val tree = container {
		image(treeBitmap)
		centerOnStage()
	}
	addApples(appleBitmap, tree)

	Score().addTo(this)

	bus.register<GameOverEvent> {
		solidRect(0, 0, RGBA(Colors.BLACK.rgb, a = 120)) {
			size(gameWidth, gameHeight)
		}
		text("Game Over", textSize = 64.0, font = font).centerOnStage()
	}

	Bird(birdBitmap, eatApple, birdSound).addTo(this).startFlying()
}

class HitBirdEvent()
class GameOverEvent()

private fun addApples(appleBitmap: Bitmap, tree: Container) {
	for (i in 0 until apples) {
		val apple = Image(appleBitmap).apply {
			name = "apple"
			size(60, 60)
			position(Random[5, 350], Random[5, 300])
		}
		tree.addChild(apple)
	}
}

class Bird(birdSpriteSheet: Bitmap, val eatApple: Sound, val birdCry: Sound) :
	Sprite(SpriteAnimation(birdSpriteSheet, spriteWidth = 126, spriteHeight = 122, columns = 4, rows = 2)) {

	private var hit = false

	init {
		playAnimationLooped(spriteDisplayTime = 200.milliseconds)

		onCollision(filter = { it is Image}) {
			if (it.name == "apple" && !hit) {
				it.removeFromParent()
				eatApple()
			}
		}
		onClick {
			hit()
		}
	}

	private fun eatApple() {
		stage?.launch {
			eatApple.play()
			apples--
			if (apples == 0) {
				bus.send(GameOverEvent())
			}
		}
	}

	suspend fun startFlying() {
		while (!hit) {
			x += 5
			delay(20.milliseconds)
			if (x > gameWidth) {
				respawn()
			}
		}
	}

	private fun respawn() {
		hit = false
		rotation = 0.degrees
		x = -50.0
		y = Random[50.0, 500.0]
	}

	private suspend fun hit() {
		bus.send(HitBirdEvent())
		birdCry.play()
		hit = true
		stage?.launch {
			tween(this::y[gameHeight], time = 1.seconds, easing = Easing.EASE_IN)
			respawn()
			startFlying()
		}
		stage?.launch {
			tween(this::rotation[Random[-40, 40].degrees], time = 100.milliseconds, easing = Easing.EASE_IN)
		}
	}
}

class Score : Container() {
	var score = 0
	val scoreText: Text = text("0", font = font, textSize = 64.0).position(gameWidth - 80, 20)

	init {
		bus.register<HitBirdEvent> { count() }
	}

	fun count() {
		scoreText.text = (++score).toString()
	}
}