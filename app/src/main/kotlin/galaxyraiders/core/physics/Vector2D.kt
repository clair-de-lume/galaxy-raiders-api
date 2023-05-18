package galaxyraiders.core.physics

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

@JsonIgnoreProperties("unit", "normal", "degree", "magnitude")
data class Vector2D(val dx: Double, val dy: Double) {
  override fun toString(): String {
    return "Vector2D(dx=$dx, dy=$dy)"
  }

  val magnitude: Double
    get() = Math.sqrt(dx.pow(2) + dy.pow(2))

  val radiant: Double
    get() = Math.atan2(dy, dx)

  val degree: Double
    get() = Math.toDegrees(radiant)

  val unit: Vector2D
    get() = Vector2D(dx / magnitude, dy / magnitude)

  val normal: Vector2D
    get() {
      val dxNormalized = dy / magnitude
      val dyNormalized = -dx / magnitude
      return Vector2D(dxNormalized, dyNormalized)
    }

  operator fun times(scalar: Double): Vector2D {
    return Vector2D(dx * scalar, dy * scalar)
  }

  operator fun div(scalar: Double): Vector2D {
    return Vector2D(dx / scalar, dy / scalar)
  }

  operator fun times(v: Vector2D): Double {
    return (dx * v.dx) + (dy * v.dy)
  }

  operator fun plus(v: Vector2D): Vector2D {
    val dxSum = dx + v.dx
    val dySum = dy + v.dy
    return Vector2D(dxSum, dySum)
  }

  operator fun plus(p: Point2D): Point2D {
    val dxMoved = dx + p.x
    val dyMoved = dy + p.y
    return Point2D(dxMoved, dyMoved)
  }

  operator fun unaryMinus(): Vector2D {
    return Vector2D(-dx, -dy)
  }

  operator fun minus(v: Vector2D): Vector2D {
    val dxMinus = dx - v.dx
    val dyMinus = dy - v.dy
    return Vector2D(dxMinus, dyMinus)
  }

  fun scalarProject(target: Vector2D): Double {
    return this * target.unit
  }

  fun vectorProject(target: Vector2D): Vector2D {
    return (this.scalarProject(target) * target.unit)
  }
}

operator fun Double.times(v: Vector2D): Vector2D {
  return Vector2D(this * v.dx, this * v.dy)
}
