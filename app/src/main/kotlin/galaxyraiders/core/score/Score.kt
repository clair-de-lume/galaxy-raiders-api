import java.time.format.DateTimeFormatter

class Score (
  var beginTime: LocalDateTime,
  var endTime: LocalDateTime,
  var score: Double = 0.0,
  var asteroidsHit: Int = 0
) {
  var beginTimeFormatted: String = beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  var endTimeFormatted: String = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

  fun increaseScore(asteroid: Asteroid){
    asteroidsHit += 1
    score += (asteroid.radius + asteroid.mass)
  }
  fun setEnd(){
    endTime = LocalDateTime.now()
  }
}
