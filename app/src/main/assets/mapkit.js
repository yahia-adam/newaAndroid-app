document.addEventListener('DOMContentLoaded', function() {
    mapkit.init({
        authorizationCallback: function(done) {
            done("eyJraWQiOiI0NzZNM1k3UTU3IiwidHlwIjoiSldUIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJSRDZERE00TjI4IiwiaWF0IjoxNzQxNzk3NjY2LCJleHAiOjE3NDI0NTM5OTl9.ebLUrsLEVBPpLnxw7vg3XrU12w6qdLMq_ZjeJ6UvKYn9MgWM8YbvUUQZhooBkd0Hn2v77u6WGn4ZMGqbX3MwXA");
        },
        language: "fr"
    });
    var map = new mapkit.Map('map');
    window.addEventListener('load', function() {
        var mapDiv = document.getElementById('map');
        mapDiv.style.width = window.innerWidth + 'px';
        mapDiv.style.height = window.innerHeight + 'px';
    });
}