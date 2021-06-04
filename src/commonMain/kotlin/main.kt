import com.soywiz.klock.milliseconds
import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.BlurFilter
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.random.get
import kotlin.random.Random

val gameWidth = 540
val gameHeight = 920

var apples = 5

suspend fun main() = Korge(width = gameWidth, height = gameHeight, bgcolor = Colors["#2b2b2b"]) {

	val backgroundBitmap = resourcesVfs["background.png"].readBitmap()
	val treeBitmap = resourcesVfs["tree.png"].readBitmap()
	val appleBitmap = resourcesVfs["apple.png"].readBitmap()
	val birdBitmap = resourcesVfs["bird.png"].readBitmap()

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

	Bird(birdBitmap).addTo(this)
}

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

class Bird(birdSpriteSheet: Bitmap) :
	Sprite(SpriteAnimation(birdSpriteSheet, spriteWidth = 126, spriteHeight = 122, columns = 4, rows = 2)) {

	init {
		playAnimationLooped(spriteDisplayTime = 200.milliseconds)
	}
}