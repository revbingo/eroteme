require(["jquery", "bootstrap", "jsrender"], function($){
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
			} else if (obj.type == 'buzzAck') {
				this_.model.buzzed(obj);
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
		this.teamListTmpl = $.templates("#teamListTmpl");
		
		this.displayTeamList = function() {
			var teamList = this_.model.getTeamList();
			$("#teams").html(this_.teamListTmpl.render(teamList));
		}
		
		$("#nextQuestion").click(function() {
			this_.model.resetBuzz();
			this_.displayTeamList();
			this_.controller.nextQuestion();
		})
	}
	
	function Model() {
		this.teams = {};
		var this_ = this; 
		
		this.updateTeams = function(teamList) {
			teamList.forEach(function(item) {
				var buzzed = false;
				if(this_.teams[item.name]) {
					buzzed = this_.teams[item.name].buzzed;
				}
				this_.teams[item.name] = item;
				this_.teams[item.name].buzzed = buzzed;
			});
			
		}
		
		this.getTeamList = function() {
			var teamList = [];
			$.each(this_.teams, function(key, value) {
				teamList.push(value);
			});
			return teamList;
		}
		
		this.buzzed = function(buzzEvent) {
			this_.teams[buzzEvent.teamName].buzzed = true;
		}
		
		this.resetBuzz = function() {
			$.each(this_.teams, function(key, value) {
				value.buzzed = false;
			});
		}
	}
		
	function NextQuestion() {
		this.type = "nextQuestion";
	}
})