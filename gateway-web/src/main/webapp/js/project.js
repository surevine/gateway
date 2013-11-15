$(document).ready(function() {
	Projects.fetch(getURLParameter("id"), function(data) {
		$('header h1').append($('<a/>', {'href': data.webUrl, 'text': data.name, 'target': '_blank'}));
		
		$('p#security-label').text(data.friendlySecurityLabel);
		
		if (!data.enabled) {
			$('p#error-label').css('display', 'block');
		}
	});
});