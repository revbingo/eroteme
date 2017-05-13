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

				switch(json.type) {
                    case "questionAnswered":
                        this_.model.questionAnswered(json);
                        this_.view.displayAnswer();
                        break;
                    case "scored":
                        this_.model.scored(json.team.score);
                        this_.view.updateScore();
                        break;
                    case "registrationResponse":
                        this_.model.scored(json.team.score);
                        this_.view.updateScore();
                        break;
                    case "ping":
                        this_.socket.send(JSON.stringify(new Pong()));
                        break;
                    case "reset":
                        this_.view.reset();
                        break;
                    case "askQuestion":
                        this_.model.nextQuestion(json);
                        this_.view.displayQuestion();
                        break;
                    case "endQuiz":
                        this_.view.endQuiz();
                        break;
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
			this_[this_.model.currentQuestion.answerType](this_.model.currentQuestion.question);
		};
		
		this.displayAnswer = function() {
			$("#questionArea").css("color", this_.model.questionCorrect ? "green" : "red");
		};
		
		this.updateScore = function() {
			$("#score").html(this_.model.teamScore);
		};

		this.reset = function() {
			$("#answerArea").empty();
		};

		this.endQuiz = function() {
			$("#questionArea").html("<h1>It's all over!</h1>");
            $("#answerArea").empty();
		};

		this.TEXT = function(currentQuestion) {
			$("#questionArea").css("color", "white");
			$("#questionArea").html(this_.model.currentQuestion.questionNumber + ": " + this_.model.currentQuestion.question);
			
			$("#answerArea").html(simpleAnswerTmpl.render([{}]));
			$("#submitAnswer").click(function() {
				controller.sendAnswer(currentQuestion.questionNumber, $("#answer").val());
				$("#answerArea").empty();
			});
		};
		
		this.VOICE = function(currentQuestion) {
			$("#answerArea").html(buzzerTmp.render([{}]));
			$("#buzzer").click(function() {
				$("#answerArea").html("...");
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
