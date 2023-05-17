package galaxyraiders.core.physics

import kotlin.math.abs
import kotlin.math.sqrt

data class Point2D(val x: Double, val y: Double) {
  operator fun plus(p: Point2D): Point2D {
    return Point2D(x + p.x, y + p.y)
  }

  operator fun plus(v: Vector2D): Point2D {
    return Point2D(x + v.dx, y + v.dy)
  }

  override fun toString(): String {
    return "Point2D(x=$x, y=$y)"
  }

  fun toVector(): Vector2D {
    return Vector2D(x,y)
  }

  fun impactVector(p: Point2D): Vector2D {
    return Vector2D(abs(x - p.x), abs(y - p.y))
  }

  fun impactDirection(p: Point2D): Vector2D {
    return Vector2D(impactVector(p).dx / impactVector(p).magnitude, impactVector(p).dy / impactVector(p).magnitude)
  }

  fun contactVector(p: Point2D): Vector2D {
    return Vector2D(impactVector(p).normal.dx, impactVector(p).normal.dy)
  }

  fun contactDirection(p: Point2D): Vector2D {
    return Vector2D(contactVector(p).dx / contactVector(p).magnitude, contactVector(p).dy / contactVector(p).magnitude)
  }
  fun distance(p: Point2D): Double {
    return (sqrt((p.x - x)*(p.x - x) + (p.y - y)*(p.y - y)))
  }
}