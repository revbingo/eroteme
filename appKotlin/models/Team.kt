package models

import play.libs.Json

class Team(val name: String, private var out: JsonWebSocket?) {
    private val NOT_BUZZED = 1000

    var score = 0
    var buzzOrder = NOT_BUZZED

    var status: Status = Status.LIVE

    var sound: String = "ping.wav"

    fun scored(delta: Int) {
        this.score += delta
    }

    fun buzzed(buzzOrder: Int) {
        this.buzzOrder = buzzOrder
    }

    fun resetBuzzer() {
        this.buzzOrder = NOT_BUZZED
    }

    fun notify(msg: Event) {
        out?.write(Json.toJson(msg))
    }

    fun rebind(msg: JsonWebSocket) {
        this.out = msg
    }

    companion object {
        fun nil(): Team {
            return Team("", null)
        }
    }

    enum class Status {
        LIVE, AWOL, GONE
    }
}