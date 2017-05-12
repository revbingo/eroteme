package models

import play.libs.Json

class Admin(private val out: JsonWebSocket) {

    fun notify(evt: Event) = out.write(Json.toJson(evt))

    fun destroy() = out.close()
}
