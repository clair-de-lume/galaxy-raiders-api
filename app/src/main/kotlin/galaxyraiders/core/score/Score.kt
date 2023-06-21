package galaxyraiders.core.score

import java.util.Date

data class Score (
  var beginDate: Date,
  var score: Double,
  var asteroidsDestroyed: Int
) {
  constructor() : this(Date(), 0.0, 0)
}