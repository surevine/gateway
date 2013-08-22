$(document).ready(function() {
	Projects.fetch(getURLParameter("name"), function(data) {
		$('header h1').append($('<a/>', {'href': data.webUrl, 'text': data.name, 'target': '_blank'}));
	});
});