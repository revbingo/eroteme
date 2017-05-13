require(["jquery", "bootstrap", "jsrender"], function($){
    new Controller();

    function Controller() {
        this.socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bindAdmin");
        this.model = new Model();
        this.view = new View(this, this.model);
        var this_ = this;

        this.socket.onmessage = function(event) {
            var obj = JSON.parse(event.data);
            if(obj.type == 'teamList') {
                this_.model.updateTeams(obj.teams);
                this_.view.displayTeamList();
            } else if (obj.type == 'question') {
                this_.model.nextQuestion = obj;
                this_.view.displayNextQuestion();
            }
        };

        this.nextQuestion = function() {
            this_.socket.send(JSON.stringify(new NextQuestion()));
        };

        this.score = function(teamName, delta) {
            this_.socket.send(JSON.stringify(new Score(teamName, delta)));
        };

        this.correct = function(teamName) {
            this_.socket.send(JSON.stringify(new Answer(teamName, true)));
        };

        this.incorrect = function(teamName) {
            this_.socket.send(JSON.stringify(new Answer(teamName, false)));
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
        };

        this.findTeamName = function(element) {
            return $(element).closest("li").attr("data-team-name");
        };

        this.displayNextQuestion = function() {
            var template = this_.nextQuestionTmpl.render(this_.model.nextQuestion);

            $("#nextQuestion").html(template);
        };

        $("#nextQuestionButton").click(function() {
            this_.displayTeamList();
            this_.controller.nextQuestion();
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

    function Answer(teamName, correct) {
        this.type = "answer";
        this.team = teamName;
        this.answerType = "voice";
        this.answerCorrect = correct;
    }
});