package models

import play.libs.Json

import java.util.Optional

class Admin(private val out: JsonWebSocket?) {

    fun notify(obj: Optional<Any>) {
        if (out != null && obj.isPresent) {
            out.write(Json.toJson(obj.get()))
        }
    }

    fun destroy() {
        out?.close()
    }
}
