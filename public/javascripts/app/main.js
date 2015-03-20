requirejs.config({
    paths: {
        bootstrap: "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min",
        jquery: "//code.jquery.com/jquery-1.11.2.min"
    },
    shim: {
        bootstrap: {
            deps: ['jquery']
        }
    }
});

require(["jquery", "bootstrap"], function ($) {
	var socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/api/bind");
	
	socket.onopen = function() {
		socket.send("ping!")
	}
	
	socket.onmessage = function(event) {
		$("#state").html("Ready!")
	}
});