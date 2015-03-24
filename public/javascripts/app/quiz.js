require(["jquery", "bootstrap"], function ($) {
	var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind");
	
	socket.onopen = function() {
		socket.send("REGISTER|" + teamName);
	}
	
	socket.onmessage = function(event) {
		$("#state").html(event.data)
	}	
});
