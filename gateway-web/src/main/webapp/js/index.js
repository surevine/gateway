$(document).ready(function() {
	Projects.displayAll();
	
	$.get('api/history/all', function(events) {
		for (var i=0; i<events.length; i++) {
			$('div#transferLog table tbody').append('<tr><td>' +new Date(1*events[i].cdate)
					+'</td><td>' +events[i].message +'</td></tr>');
		}
	});
});