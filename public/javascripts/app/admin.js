require(["jquery", "bootstrap"], function($){
	var view = new View();
	var controller = new Controller(view);
	view.setController(controller);
	
	function Controller(view) {
		this.socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind?teamName=");
		this.view = view;
		var this_ = this;
		
		this.socket.onmessage = function(event) {
			var obj = JSON.parse(event.data);
			if(obj.type == 'teamList') {
				this_.view.displayTeamList(obj.teams)
			}
		}
		
		this.nextQuestion = function() {
			this_.socket.send(JSON.stringify(new NextQuestion()));
		}
		
	}
	
	function View() {
		var this_ = this;
		
		this.setController = function(controller) {
			this_.controller = controller;
		}
		
		this.displayTeamList = function(teams) {
			$("#teams").html("");
			teams.forEach(function(team) {
				$("#teams").append($("<li />").html(team));
			})
		}
		
		$("#nextQuestion").click(function() {
			this_.controller.nextQuestion()
		})
	}
	
	function NextQuestion() {
		this.type = "nextQuestion";
	}
})