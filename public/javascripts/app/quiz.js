require(["jquery", "bootstrap", "jsrender"], function ($) {
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
					this_.model.answerReceived(json)
					this_.view.displayAnswer();
					this_.view.updateScore();
				} else {
					if(json.answerType) {
						this_.model.nextQuestion(json);
						this_.view.displayQuestion();
					}	
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
		var simpleAnswerTmpl = $.templates("#simpleAnswer");
		
		this.displayQuestion = function() {
			$("#questionArea").css("color", "white");
			$("#questionArea").html(this_.model.currentQuestion.questionNumber + ": " + this_.model.currentQuestion.question);
			
			this_[this_.model.currentQuestion.answerType](this_.model.currentQuestion);
		}
		
		this.displayAnswer = function() {
			$("#questionArea").css("color", this_.model.questionCorrect ? "green" : "red");
		}
		
		this.updateScore = function() {
			$("#score").html(this_.model.teamScore);
		}
		
		this.SIMPLE = function(currentQuestion) {
			$("#answerArea").html(simpleAnswerTmpl.render([{}]));
			$("#submitAnswer").click(function() {
				controller.sendAnswer(currentQuestion.questionNumber, $("#answer").val());
				$("#answerArea").empty();
			})
		}
		
		this.debug = function(data) {
			$("#debug").html(JSON.stringify(data));
		}
	}

	function Model() {
		this.currentQuestion = {};
		this.teamScore = 0;
		this.questionCorrect = false;
		
		this.nextQuestion = function(question) {
			this.currentQuestion = question;
		}
		
		this.answerReceived = function(answer) {
			this.teamScore = answer.score;
			this.questionCorrect = answer.correct;
		}
	}
	
	function Answer(questionNumber, answer) {
		this.answer = answer;
		this.questionNumber = questionNumber;
		this.type = "answer";
	}
});