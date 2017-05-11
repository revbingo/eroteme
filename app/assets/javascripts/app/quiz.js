require(["jquery", "bootstrap", "jsrender"], function ($) {
	new Controller();
	
	function Controller() {
		this.socket = register();
		this.model = new Model();
		this.view = new View(this, this.model);
		var this_ = this;
		
		function register() {
			var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=" + teamName);
			
			socket.onmessage = function(event) {
				var json = JSON.parse(event.data);
			//	this_.view.debug(json);
				
				if(json.type == "questionAnswered") {
					this_.model.questionAnswered(json);
					this_.view.displayAnswer();
				} else if (json.type == "scored") {
					this_.model.scored(json.score);
					this_.view.updateScore();
				} else if (json.type == "registrationResponse") {
					this_.model.scored(json.team.score);
					this_.view.updateScore();
                } else if (json.type == "ping") {
                    this_.socket.send(JSON.stringify(new Pong()));
                } else if (json.type == "reset") {
                     this_.view.reset();
				} else {
					if(json.answerType) {
						this_.model.nextQuestion(json);
						this_.view.displayQuestion();
					}	
				}
				
			};
			
			socket.onclose = function() {
				this_.socket = register();
			};
			return socket;
		}
		
		this.sendAnswer = function(questionNumber, answer) {
			this_.socket.send(JSON.stringify(new Answer(questionNumber, answer)));
		};
		
		this.buzz = function(questionNumber) {
			this_.socket.send(JSON.stringify(new Buzz(questionNumber)));
		};
	}
		
	function View(controller, model) {
		
		this.controller = controller;
		this.model = model;
		var this_ = this;
		var simpleAnswerTmpl = $.templates("#simpleAnswer");
		var buzzerTmp = $.templates("#buzzer");
		
		this.displayQuestion = function() {
			this_[this_.model.currentQuestion.answerType](this_.model.currentQuestion);
		};
		
		this.displayAnswer = function() {
			$("#questionArea").css("color", this_.model.questionCorrect ? "green" : "red");
		};
		
		this.updateScore = function() {
			$("#score").html(this_.model.teamScore);
			var audio = new Audio("/assets/sounds/cheer.wav");
			audio.play();
		};

		this.reset = function() {
            $("#answerArea").empty();
		};

		this.SIMPLE = function(currentQuestion) {
			$("#questionArea").css("color", "white");
			$("#questionArea").html(this_.model.currentQuestion.questionNumber + ": " + this_.model.currentQuestion.question);
			
			$("#answerArea").html(simpleAnswerTmpl.render([{}]));
			$("#submitAnswer").click(function() {
				controller.sendAnswer(currentQuestion.questionNumber, $("#answer").val());
				$("#answerArea").empty();
			});
		};
		
		this.BUZZER = function(currentQuestion) {
			$("#answerArea").html(buzzerTmp.render([{}]));
			$("#buzzer").click(function() {
				$("#answerArea").html("...");
				new Audio("/assets/sounds/ping.wav").play();
				controller.buzz(currentQuestion.questionNumber);
			});
		};
		
		this.debug = function(data) {
			$("#debug").html(JSON.stringify(data));
		};
	}

	function Model() {
		this.currentQuestion = {};
		this.teamScore = 0;
		this.questionCorrect = false;
		
		var this_ = this;
		
		this.nextQuestion = function(question) {
			this_.currentQuestion = question;
		};
		
		this.questionAnswered = function(answer) {
			this_.questionCorrect = answer.correct;
		};
		
		this.scored = function(score) {
			this_.teamScore = score;
		};
	}
	
	function Answer(questionNumber, answer) {
		this.answer = answer;
		this.questionNumber = questionNumber;
		this.type = "answer";
	}
	
	function Buzz(questionNumber) {
		this.type = "buzz";
		this.questionNumber = questionNumber;
	}

    function Pong() {
        this.type = "pong";
    }
});
