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
			}
		};
		
		this.nextQuestion = function() {
			this_.socket.send(JSON.stringify(new NextQuestion()));
		};
		
		this.score = function(teamName, delta) {
			this_.socket.send(JSON.stringify(new Score(teamName, delta)));
		};
		
	}
	
	function View(controller, model) {
		var this_ = this;
		this.controller = controller;
		this.model = model;
		this.teamListTmpl = $.templates("#teamListTmpl");
		
		this.displayTeamList = function() {
			var sortedTeamList = this_.model.teams.sort(function(a,b) { 
				return a.buzzOrder - b.buzzOrder; 
			});
			
			var template = this_.teamListTmpl.render(sortedTeamList);
			$("#teams").html(template);
			
			$("#teams > li > .plusScore").click(function() {
				this_.controller.score($(this).parent("li").attr("data-team-name"), 1);
			});
			
			$("#teams > li > .minusScore").click(function() {
				this_.controller.score($(this).parent("li").attr("data-team-name"), -1);
			});
		};
		
		$("#nextQuestion").click(function() {
			this_.displayTeamList();
			this_.controller.nextQuestion();
		});
		
	}
	
	function Model() {
		this.teams = [];

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
});