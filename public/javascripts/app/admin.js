require(["jquery", "bootstrap"], function($){
	var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=");

	socket.onmessage = function(event) {
		var obj = JSON.parse(event.data);
		if(obj.type == 'teamList') {
			$("#teams").html("");
			obj.teams.forEach(function(team) {
				$("#teams").append($("<li />").html(team));
			})
		}
	}
	
	$("#nextQuestion").click(function() {
		socket.send(JSON.stringify(new NextQuestion()));
	})
	
	function NextQuestion() {
		this.type = "nextQuestion";
	}
	
})