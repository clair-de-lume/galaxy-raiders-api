package galaxyraiders.core.game
import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D
import java.util.Timer
import kotlin.concurrent.schedule

class Explosion(
  initialPosition: Point2D,
  radius: Double
) :
  SpaceObject("Explosion", '*', initialPosition, Vector2D(0.0, 0.0), radius, mass = 0.0) {
  var isTriggered: Boolean = true
    private set

  private var timer: Timer = Timer("schedule", true);
  init {
    timer.schedule(2000) {
      isTriggered = false
    }
  }
}
