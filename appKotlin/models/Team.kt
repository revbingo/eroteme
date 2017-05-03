package models

import play.libs.Json
import java.util.*

class Team(val name: String?, private var out: JsonWebSocket?) {
    var score = 0
        private set
    var buzzOrder = NOT_BUZZED
        private set

    fun scored(delta: Int) {
        this.score += delta
    }

    fun buzzed(buzzOrder: Int) {
        this.buzzOrder = buzzOrder
    }

    fun resetBuzzer() {
        this.buzzOrder = NOT_BUZZED
    }

    fun haveBuzzed(): Boolean {
        return this.buzzOrder > 0
    }

    fun notify(obj: Optional<Any>) {
        if (out != null && obj.isPresent) {
            out!!.write(Json.toJson(obj.get()))
        }
    }

    fun rebind(out: JsonWebSocket) {
        this.out = out
    }

    companion object {

        private val NOT_BUZZED = 1000

        fun nil(): Team {
            return Team(null, null)
        }
    }

}
