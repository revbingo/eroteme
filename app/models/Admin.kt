package models

class Admin(private val out: JsonWebSocket) {

    fun notify(evt: Event) = out.write(evt.asJson())

    fun destroy() = out.close()
}
