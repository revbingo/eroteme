requirejs.config({
	baseUrl: 'assets/javascripts',
    paths: {
        bootstrap: "bootstrap.min",
        jquery: "jquery-1.11.2.min"
    },
    shim: {
        bootstrap: {
            deps: ['jquery']
        }
    }
});

require(["jquery", "bootstrap"]);