require(["jquery", "bootstrap"], function ($) {
	var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=" + teamName);
	
	socket.onmessage = function(event) {
		$("#state").html(event.data)
	}
	
	function RegistrationMessage(teamName) {
		this.type = "REGISTER";
		this.teamName = teamName;
		
		return this;
	}
});
