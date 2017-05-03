package models

import play.libs.Json

class Admin(private val out: JsonWebSocket) {

    fun notify(msg: Domain) {
        out.write(Json.toJson(msg))
    }

    fun destroy() {
        out.close()
    }
}
