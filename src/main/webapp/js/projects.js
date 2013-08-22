var CTX = '/gateway';

function ProjectsClass() {
	var drawAll = function(data) {
		for (var i = 0; i < data.length; i++) {
			$('div#projects table').append('<tr><td><a href="project.html?name='
					+data[i].name +'">' +data[i].name
					+'</a></td><td>FLAGGED</td><td><input type="checkbox" checked="checked"/></td></tr>')
		}
	};
	
	this.fetch = function(name, callback) {
		$.ajax({
			url: CTX +'/api/projects/' +name
		}).done(function(data) {
			callback(data);
		});
	};
	
	this.fetchAll = function(callback) {
		$.ajax({
			url: CTX +'/api/projects/'
		}).done(function(data) {
			callback(data);
		});
	};

	this.displayAll = function() {
		this.fetchAll(drawAll);
	};
};

var Projects = new ProjectsClass();