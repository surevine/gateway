var CTX = '/gateway';

function ProjectsClass() {
	var drawAll = function(data) {
		for (var i = 0; i < data.length; i++) {
			$('div#projects table').append('<tr id="row-'
					+data[i].name
					+'"><td><a href="project.html?id='
					+data[i].id +'">' +data[i].name
					+'</a></td><td>FLAGGED</td><td><input type="checkbox" onchange="Projects.check(this, \''
					+data[i].id
					+'\')"'
					+(data[i].enabled ? ' checked="checked"' : '')
					+'"/></td></tr>');
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
	
	this.check = function(e, id) {
		$.ajax({
			type: 'PUT',
			url: CTX +'/api/projects/enabled/' +id,
			data: {
				'enabled': e.checked
			}
		});
	};
};

var Projects = new ProjectsClass();