require(["jquery", "bootstrap"], function ($) {
	var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=" + teamName);
	
	socket.onmessage = function(event) {
		$("#state").html(event.data)
	}	
	
	socket.onclose = function(event) {
		alert("oh, I lost connection");
	}
});
