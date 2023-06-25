package galaxyraiders.core.score
import galaxyraiders.core.game.Asteroid
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Score {
  var beginTime: String
  var endTime: String
  var score: Double
  var asteroidsHit: Int

  constructor(beginTime: String, endTime: String, score: Double, asteroidsHit: Int){
    this.beginTime = beginTime
    this.endTime = endTime
    this.score = 0.0
    this.asteroidsHit = 0
  }

  fun increaseScore(asteroid: Asteroid){
    asteroidsHit += 1
    score += (asteroid.radius + asteroid.mass)
  }
  fun setEnd(){
    endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  }
}
