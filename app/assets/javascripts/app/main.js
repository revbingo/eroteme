requirejs.config({
	baseUrl: 'assets/javascripts',
    paths: {
        bootstrap: "bootstrap.min",
        jquery: "jquery-1.11.2.min",
        jsrender: "jsrender.min"
    },
    shim: {
        bootstrap: {
            deps: ['jquery']
        },
        jsrender: {
            deps: ['jquery']
        }
    }
});

require(["jquery", "bootstrap", "jsrender"]);