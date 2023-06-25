package galaxyraiders.core.game

import galaxyraiders.Config
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Controller.PlayerCommand
import galaxyraiders.ports.ui.Visualizer
import kotlin.system.measureTimeMillis
import java.time.LocalDateTime
import java.io.File
import kotlin.collections.List
import com.beust.klaxon.Klaxon
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import galaxyraiders.core.score.Score
import com.beust.klaxon.Parser
import java.time.format.DateTimeFormatter

const val MILLISECONDS_PER_SECOND: Int = 1000

object GameEngineConfig {
  private val config = Config(prefix = "GR__CORE__GAME__GAME_ENGINE__")

  val frameRate = config.get<Int>("FRAME_RATE")
  val spaceFieldWidth = config.get<Int>("SPACEFIELD_WIDTH")
  val spaceFieldHeight = config.get<Int>("SPACEFIELD_HEIGHT")
  val asteroidProbability = config.get<Double>("ASTEROID_PROBABILITY")
  val coefficientRestitution = config.get<Double>("COEFFICIENT_RESTITUTION")

  val msPerFrame: Int = MILLISECONDS_PER_SECOND / this.frameRate
}

@Suppress("TooManyFunctions")
class GameEngine(
  val generator: RandomGenerator,
  val controller: Controller,
  val visualizer: Visualizer,
) {
  val field = SpaceField(
    width = GameEngineConfig.spaceFieldWidth,
    height = GameEngineConfig.spaceFieldHeight,
    generator = generator
  )

  var beginTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  var endTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  var score = Score(beginTime, endTime, 0.0, 0)
  val scoreboard = File("src/main/kotlin/galaxyraiders/core/score/Scoreboard.json")
  val leaderboard = File("src/main/kotlin/galaxyraiders/core/score/Leaderboard.json")

  fun clearFileContents(file: File) {
    if (file.exists() && file.length() > 0) {
      file.writeText("")
    }
  }

  var playing = true

  fun execute() {

    val scorejson = """
    {
        "beginTime": ${score.beginTime},
        "endTime": ${score.endTime},
        "score": ${score.score},
        "asteroidsHit": ${score.asteroidsHit}
    }
""".trimIndent()

    clearFileContents(scoreboard)
    scoreboard.writeText(scorejson)

    while (true) {
      val duration = measureTimeMillis { this.tick() }

      Thread.sleep(
        maxOf(0, GameEngineConfig.msPerFrame - duration)
      )
    }
  }

  fun execute(maxIterations: Int) {
    repeat(maxIterations) {
      this.tick()
    }
  }

  fun tick() {
    this.processPlayerInput()
    this.updateSpaceObjects()
    this.renderSpaceField()
    this.updateScoreBoardJson()
    this.updateLeaderboardJson()
  }

  fun processPlayerInput() {
    this.controller.nextPlayerCommand()?.also {
      when (it) {
        PlayerCommand.MOVE_SHIP_UP ->
          this.field.ship.boostUp()
        PlayerCommand.MOVE_SHIP_DOWN ->
          this.field.ship.boostDown()
        PlayerCommand.MOVE_SHIP_LEFT ->
          this.field.ship.boostLeft()
        PlayerCommand.MOVE_SHIP_RIGHT ->
          this.field.ship.boostRight()
        PlayerCommand.LAUNCH_MISSILE ->
          this.field.generateMissile()
        PlayerCommand.PAUSE_GAME ->
          this.playing = !this.playing
      }
    }
  }

  fun updateSpaceObjects() {
    if (!this.playing) return
    this.handleCollisions()
    this.moveSpaceObjects()
    this.trimSpaceObjects()
    this.generateAsteroids()
  }

  fun handleCollisions() {
    this.field.spaceObjects.forEachPair {
        (first, second) ->
      if (first.impacts(second)) {
        if (first is Asteroid && second is Missile) {
          score.increaseScore(first)
          this.field.destroyMissile(second)
          this.field.generateExplosion(first)
        }
        else if (first is Missile && second is Asteroid) {
          score.increaseScore(second)
          this.field.destroyMissile(first)
          this.field.generateExplosion(second)
        }
        first.collideWith(second, GameEngineConfig.coefficientRestitution)
      }
    }
  }

  fun updateScoreBoardJson(){
    val jsonString = this.scoreboard.readText()
    val parser = Parser.default()
    val json = parser.parse(StringBuilder(jsonString)) as? JsonArray<*>

    if (json != null && json.isNotEmpty()) {
      val entry = json[0] as? JsonObject
      if (entry != null) {
        // Modify the desired parameters in the first entry
        score.setEnd()
        entry["endTime"] = score.endTime
        entry["score"] = score.score
        entry["asteroidsHit"] = score.asteroidsHit
      }
      // Write the updated JSON back to the file
      val updatedJsonString = Klaxon().toJsonString(json)
      scoreboard.writeText(updatedJsonString)
    }
  }

  fun updateLeaderboardJson() {
    val scores = mutableListOf<Score>()

    if (this.leaderboard.exists() && this.leaderboard.length() > 0) {
      val jsonFilePath = "src/main/kotlin/galaxyraiders/core/score/Leaderboard.json"
      val jsonArray = Parser.default().parse(StringBuilder(jsonFilePath)) as JsonArray<JsonObject>

      jsonArray.forEach { jsonObject ->
        val beginTime: String = jsonObject.string("beginTime") ?: ""
        val endTime: String = jsonObject.string("endTime") ?: ""
        val score: Double = jsonObject.double("score") ?: 0.0
        val asteroidsHit: Int = jsonObject.int("asteroidsHit") ?: 0

        val temp = Score(beginTime, endTime, score, asteroidsHit)
        scores.add(temp)
      }
    }
    else {
      val jsonFilePath = "src/main/kotlin/galaxyraiders/core/score/Scoreboard.json"
      val jsonArray = Parser.default().parse(StringBuilder(jsonFilePath)) as JsonArray<JsonObject>

      jsonArray.forEach { jsonObject ->
        val beginTime: String = jsonObject.string("beginTime") ?: ""
        val endTime: String = jsonObject.string("endTime") ?: ""
        val score: Double = jsonObject.double("score") ?: 0.0
        val asteroidsHit: Int = jsonObject.int("asteroidsHit") ?: 0

        val temp = Score(beginTime, endTime, score, asteroidsHit)
        scores.add(temp)
      }
    }

    val leaders = scores.sortedByDescending { it.score }
    for(i in 0..2) {
      val updatedJsonString = Klaxon().toJsonString(leaders[i])
      leaderboard.writeText(updatedJsonString)
    }
  }

  fun moveSpaceObjects() {
    this.field.moveShip()
    this.field.moveAsteroids()
    this.field.moveMissiles()
  }

  fun trimSpaceObjects() {
    this.field.trimAsteroids()
    this.field.trimMissiles()
    this.field.trimExplosions()
  }

  fun generateAsteroids() {
    val probability = generator.generateProbability()

    if (probability <= GameEngineConfig.asteroidProbability) {
      this.field.generateAsteroid()
    }
  }

  fun renderSpaceField() {
    this.visualizer.renderSpaceField(this.field)
  }
}

fun <T> List<T>.forEachPair(action: (Pair<T, T>) -> Unit) {
  for (i in 0 until this.size) {
    for (j in i + 1 until this.size) {
      action(Pair(this[i], this[j]))
    }
  }
}
