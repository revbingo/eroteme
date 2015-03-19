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

//Define dependencies and pass a callback when dependencies have been loaded
require(["bootstrap", "jquery"], function ($) {
    //Bootstrap and jquery are ready to use here
    //Access jquery and bootstrap plugins with $ variable
});	