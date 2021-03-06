
app.controller('fsdbController', function($scope, $http){
	
	var siq = this;
	siq.undo = [];
	
	$http.get('http://192.168.99.100:8080/api/v0/entries.json')
		.then(function(response){
			siq.data = response.data;
		});
	siq.index = -1;
	siq.panelNum = -1;
	siq.editNum = -1;
	siq.upsertEntry = function(subject, content){
		if(siq.operation === 'New Entry'){
			siq.postEntry(siq.data.length, subject, content);
		}
		else{
			siq.updateEntry(siq.index, subject, content);
		}
	};

	siq.editClick = function(index)
	{
		siq.operation = 'Edit Entry'; 
		siq.index = index;
		siq.siqSubject = siq.data[index].subject;
		siq.siqContent = siq.data[index].content;
	};

	siq.getEntry = function(index){
		siq.panelNum = siq.panelNum == index ? -1 : index;
		var id = siq.data[index].id;
		console.log('getting entry ' + id);

		$http.get('http://192.168.99.100:8080/api/v0/entries/' + id + '.json')
			.then(function(response){
				siq.data[index] = response.data;
			});
	};

	siq.updateEntry = function(index, subject, content){
		var id = siq.data[index].id;
		var entry = {};
		entry.id = id;
		entry.subject = subject;
		entry.content = content;
		siq.data[index] = entry;
		siq.clear();
		$http.put('http://192.168.99.100:8080/api/v0/entries/' + id + '.json', entry)
			.then(function(response){
				console.log("update finished with status '" + response.data + "'");
			});
        siq.editNum = -1;
	};

	siq.deleteEntry = function(index){
		console.log('deleting ' + index + '...');
		var id = siq.data[index].id;
		var element = siq.data.splice(index, 1)[0];

		$http.get('http://192.168.99.100:8080/api/v0/entries/' + id + '.json')
			.then(function(response){
				element = response.data;
				element.index = index;
				siq.undo.push(element);
			});

		siq.panelNum = -1;
		$http.delete('http://192.168.99.100:8080/api/v0/entries/' + id)
			.then(function(response){
				console.log("delete finished with status '" + response.data + "'");
			});
	};

	siq.postEntry = function(subject, content){
		// {"subject":"Something else","content":"This is the content for 'Something else'"}
		var entry = {};
		entry.subject = subject;
		entry.content = content;
		siq.clear();
		console.log(entry);
		$http.post('http://192.168.99.100:8080/api/v0/entries.json', entry)
			.then(function(res){
				console.log(`success:${res.data}`);
				entry.id = res.data;
				siq.data.splice(siq.data.length, 0, entry);
			}, function(err){
				console.log(`error: ${err.data}`);
			});
	};

	siq.Undo = function(){
		var element = siq.undo.pop();
		siq.postEntry(element.index, element.subject, element.content);
	};

	siq.clear = function(){
		siq.siqContent = "";
		siq.siqSubject = "";
	};
});