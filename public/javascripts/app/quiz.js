require(["jquery", "bootstrap"], function ($) {
	var view = new View();
	var controller = new Controller(view);
	view.setController(controller);
	
	function Controller(view) {
		this.socket = register();
		this.view = view;
		var this_ = this;
		
		function register() {
			var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=" + teamName);
			return socket;
		}
		
		this.socket.onmessage = function(event) {
			var json = JSON.parse(event.data);
			this_.view.debug(json);
			
			if(json.answerType) {
				this_.view[json.answerType](json);
			}
		}
		
		this.socket.onclose = function(event) {
			this_.socket = register();
		}
		
		this.sendAnswer = function(questionNumber, answer) {
			this_.socket.send(JSON.stringify(new Answer(questionNumber, answer)));
		}
		
	}
		
	function View() {
		
		this.controller = null;
		var this_ = this;
		
		this.setController = function(controller) {
			this_.controller = controller;
		}
				
		this.SIMPLE = function(json) {
			$("#answerArea").html('<input type="text" id="answer"></input><button id="submitAnswer">Go!</button>');
			$("#submitAnswer").click(function() {
				controller.sendAnswer(json.questionNumber, $("#answer").val());
			})
		}
		
		this.debug = function(data) {
			$("#state").html(JSON.stringify(data));
		}
	}
	
	function Answer(questionNumber, answer) {
		this.answer = answer;
		this.questionNumber = questionNumber;
		this.type = "answer";
	}
});