package models

import play.libs.Json

class Admin(private val out: JsonWebSocket) {

    fun notify(obj: Domain) {
        out.write(Json.toJson(obj))
    }

    fun destroy() {
        out.close()
    }
}
