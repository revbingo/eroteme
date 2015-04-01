require(["jquery", "bootstrap"], function ($) {
	var controller = new Controller();
	
	function Controller(view) {
		this.socket = register();
		this.model = new Model();
		this.view = new View(this, this.model);
		var this_ = this;
		
		function register() {
			var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=" + teamName);
			
			socket.onmessage = function(event) {
				var json = JSON.parse(event.data);
				this_.view.debug(json);
				
				if(json.type == "answerResponse") {
					this_.view.displayAnswer(json);
				}
				if(json.answerType) {
					this_.model.nextQuestion(json);
					this_.view.displayQuestion();
					this_.view[json.answerType](json);
				}
			}
			
			socket.onclose = function(event) {
				this_.socket = register();
			}
			return socket;
		}
		
		this.sendAnswer = function(questionNumber, answer) {
			this_.socket.send(JSON.stringify(new Answer(questionNumber, answer)));
		}
	}
		
	function View(controller, model) {
		
		this.controller = controller;
		this.model = model;
		var this_ = this;
		
		this.displayQuestion = function() {
			$("#questionArea").css("color", "white");
			$("#questionArea").html(this_.model.currentQuestion.questionNumber + ": " + this_.model.currentQuestion.question);
		}
		
		this.displayAnswer = function(json) {
			$("#questionArea").css("color", json.correct ? "green" : "red");
		}
		
		this.SIMPLE = function(json) {
			$("#answerArea").html('<input type="text" id="answer"></input><button id="submitAnswer">Go!</button>');
			$("#submitAnswer").click(function() {
				controller.sendAnswer(json.questionNumber, $("#answer").val());
				$("#answerArea").empty();
			})
		}
		
		this.debug = function(data) {
			$("#state").html(JSON.stringify(data));
		}
	}

	function Model() {
		this.currentQuestion = {};
		
		this.nextQuestion = function(question) {
			this.currentQuestion = question;
		}
	}
	
	function Answer(questionNumber, answer) {
		this.answer = answer;
		this.questionNumber = questionNumber;
		this.type = "answer";
	}
});