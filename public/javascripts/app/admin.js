require(["jquery", "bootstrap"], function($){
	var controller = new Controller();
	
	function Controller() {
		this.socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bindAdmin");
		this.model = new Model();
		this.view = new View(this, this.model);
		var this_ = this;
		
		this.socket.onmessage = function(event) {
			var obj = JSON.parse(event.data);
			if(obj.type == 'teamList') {
				this_.model.updateTeams(obj.teams);
				this_.view.displayTeamList()
			}
		}
		
		this.nextQuestion = function() {
			this_.socket.send(JSON.stringify(new NextQuestion()));
		}
		
	}
	
	function View(controller, model) {
		var this_ = this;
		this.controller = controller;
		this.model = model;
		
		this.displayTeamList = function() {
			$("#teams").html("");
			this_.model.teams.forEach(function(team) {
				$("#teams").append($("<li />").addClass("list-group-item").html(team.name + ":" + team.score));
			})
		}
		
		$("#nextQuestion").click(function() {
			this_.controller.nextQuestion()
		})
	}
	
	function Model() {
		this.teams = [];
		var this_ = this; 
		
		this.updateTeams = function(teamList) {
			this_.teams = teamList;
		}
	}
		
	function NextQuestion() {
		this.type = "nextQuestion";
	}
})