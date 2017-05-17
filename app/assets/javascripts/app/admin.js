require(["jquery", "bootstrap", "jsrender"], function($){
    new Controller();

    function Controller() {
        this.socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bindAdmin");
        this.model = new Model();
        this.view = new View(this, this.model);
        var this_ = this;

        this.socket.onmessage = function(event) {
            var obj = JSON.parse(event.data);
            switch(obj.type) {
                case 'quizState':
                    this_.model.updateTeams(obj.teams);
                    this_.view.displayTeamList();
                    break;
                case 'askQuestion':
                    this_.model.nextQuestion = obj.question;
                    this_.view.displayNextQuestion();
                    break;
                case 'endQuiz':
                    this_.view.endQuiz();
                    break;
                case 'buzzAck':
                    new Audio("/assets/sounds/" + obj.team.sound + ".wav").play();
                    break;
            }
        };

        this.nextQuestion = function() {
            this_.view.displayTeamList();
            this_.send(new NextQuestion());
        };

        this.score = function(teamName, delta) {
            this_.send(new Score(teamName, delta));
        };

        this.correct = function(teamName) {
            this_.send(new Confirmation(teamName, true));
            this_.view.reset();
        };

        this.incorrect = function(teamName) {
            this_.send(new Confirmation(teamName, false));
        };

        this.remove = function(teamName) {
            this_.send(new Removal(teamName));
        };

        this.send = function(message) {
            this_.socket.send(JSON.stringify(message));
        };
    }

    function View(controller, model) {
        var this_ = this;
        this.controller = controller;
        this.model = model;
        this.teamListTmpl = $.templates("#teamListTmpl");
        this.nextQuestionTmpl = $.templates("#nextQuestionTmpl");

        this.displayTeamList = function() {
            var sortedTeamList = this_.model.teams.sort(function(a,b) {
                return a.buzzOrder - b.buzzOrder;
            });

            var template = this_.teamListTmpl.render(sortedTeamList);
            $("#teams").html(template);

            $(".plusScore").click(function() {
                this_.controller.score(this_.findTeamName(this), 1);
            });

            $(".minusScore").click(function() {
                this_.controller.score(this_.findTeamName(this), -1);
            });

            $(".correct").click(function() {
                this_.controller.correct(this_.findTeamName(this));
            });

            $(".incorrect").click(function() {
                this_.controller.incorrect(this_.findTeamName(this));
            });

            $("#removeTeam").click(function() {
                this_.controller.remove(this_.findTeamName(this));
            });
        };

        this.findTeamName = function(element) {
            return $(element).closest("li").attr("data-team-name");
        };

        this.displayNextQuestion = function() {
            var template = this_.nextQuestionTmpl.render(this_.model.nextQuestion);

            $("#nextQuestion").html(template);
        };

        this.reset = function() {
            $("#nextQuestion").empty();
        };

        this.endQuiz = function() {
            $("#nextQuestion").html("<h1>It's all over!</h1>");
            $("#nextQuestionButton").remove();
        };

        $("#nextQuestionButton").click(function() {
            this_.controller.nextQuestion();
        });

        $(document).keydown(function(e) {
            if(e.which == 32) {
                this_.controller.nextQuestion();
            }
        });

    }

    function Model() {
        this.teams = [];
        this.nextQuestion = {};

        this.updateTeams = function(teamList) {
            this.teams = teamList;
        };
    }

    function NextQuestion() {
        this.type = "nextQuestion";
    }

    function Score(teamName, delta) {
        this.type = "score";
        this.team = teamName;
        this.delta = delta;
    }

    function Confirmation(teamName, correct) {
        this.type = "confirmation";
        this.team = teamName;
        this.correct = correct;
    }

    function Answer(teamName, questionNumber, correct) {
        this.type = "answer";
        this.team = teamName;
        this.answerType = "voice";
        this.questionNumber = questionNumber;
        this.answer = correct.toString();
    }

    function Removal(teamName) {
        this.type = "removal";
        this.team = teamName;
    }
});