package galaxyraiders.core.physics

import kotlin.math.pow
import kotlin.math.sqrt

data class Point2D(val x: Double, val y: Double) {
  operator fun plus(p: Point2D): Point2D {
    val pointsSumX = p.x + x
    val pointsSumY = p.y + y
    return Point2D(pointsSumX, pointsSumY)
  }

  operator fun plus(v: Vector2D): Point2D {
    val vectorPointSumX = v.dx + x
    val vectorPointSumY = v.dy + y
    return Point2D(vectorPointSumX, vectorPointSumY)
  }

  override fun toString(): String {
    return "Point2D(x=$x, y=$y)"
  }

  fun toVector(): Vector2D {
    return Vector2D(x, y)
  }

  fun impactVector(p: Point2D): Vector2D {
    val absoluteDistanceX = Math.abs(x - p.x)
    val absoluteDistanceY = Math.abs(y - p.y)
    return Vector2D(absoluteDistanceX, absoluteDistanceY)
  }

  fun impactDirection(p: Point2D): Vector2D {
    val impactVector = impactVector(p)
    val direction = impactVector.unit
    return direction
  }

  fun contactVector(p: Point2D): Vector2D {
    val impactVector = impactVector(p)
    val contactVector = impactVector.normal
    return contactVector
  }

  fun contactDirection(p: Point2D): Vector2D {
    val impactDirection = impactDirection(p)
    val contactDirection = impactDirection.normal
    return contactDirection
  }

  fun distance(p: Point2D): Double {
    val xDistance = (p.x - x).pow(2)
    val yDistance = (p.y - y).pow(2)
    val pointDistance = sqrt(xDistance + yDistance)
    return pointDistance
  }
}
