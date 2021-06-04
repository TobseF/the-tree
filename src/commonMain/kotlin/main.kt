import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.BlurFilter
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing

val gameWidth = 540
val gameHeight = 920

suspend fun main() = Korge(width = gameWidth, height = gameHeight, bgcolor = Colors["#2b2b2b"]) {

	val backgroundBitmap = resourcesVfs["background.png"].readBitmap()

	image(backgroundBitmap) {
		filter = BlurFilter(2.5)
		size(gameWidth, gameHeight)
	}

}