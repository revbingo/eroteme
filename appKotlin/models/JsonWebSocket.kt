package models

import com.fasterxml.jackson.databind.JsonNode

import play.mvc.WebSocket

class JsonWebSocket(private val wrapped: WebSocket.Out<JsonNode>) {

    fun get(): WebSocket.Out<JsonNode> {
        return this.wrapped
    }

    fun write(json: JsonNode) {
        wrapped.write(json)
    }

    fun close() {
        wrapped.close()
    }
}

