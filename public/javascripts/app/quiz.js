require(["jquery", "bootstrap"], function ($) {
	var socket = register();
	
	function register() {
		var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=" + teamName);
		return socket;	
	}
	socket.onmessage = function(event) {
		$("#state").html(event.data)
	}	
	
	socket.onclose = function(event) {
		alert("lost connection - reregistering");
		socket = register;
	}
});
