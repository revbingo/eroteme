require(["jquery", "bootstrap"], function ($) {
	var socket = register();
	
	function register() {
		var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=" + teamName);
		return socket;	
	}
	
	socket.onmessage = function(event) {
		$("#state").html(event.data);
		
		var json = JSON.parse(event.data);
		if(json.answerType == "SIMPLE") {
			$("#answerArea").html('<input type="text" id="answer"></input><button id="submitAnswer">Go!</button>');
			$("#submitAnswer").click(function() {
				socket.send(JSON.stringify(new Answer(json.questionNumber, $("#answer").val())));
			})
		}
	}
	
	socket.onclose = function(event) {
		alert("lost connection - reregistering");
		socket = register();
	}
	
	function Answer(questionNumber, answer) {
		this.answer = answer;
		this.questionNumber = questionNumber;
		this.type = "answer";
	}
});