package models

class Team(val name: String) {
    private var out: JsonWebSocket? = null
    private val NOT_BUZZED = 1000

    var score = 0
    var buzzOrder = NOT_BUZZED

    var latestResponse: String? = null
    var status: Status = Status.JOINING

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
        out?.write(msg.asJson())
    }

    fun bind(msg: JsonWebSocket) {
        this.out = msg
    }

    companion object {
        fun nil(): Team {
            return Team("")
        }
    }

    enum class Status {
        JOINING, LIVE, AWOL, GONE
    }
}