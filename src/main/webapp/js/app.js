function Person(data) {
	var self = this;
	self.id = data.id;
	self.name = ko.observable(data.name);
	self.address = ko.observable(data.address);
	self.age = ko.observable(data.age);
}

function PersonViewModel() {
	var self = this;
	self.people = ko.observableArray([]);

	// new person input place holder
	self.newPersonName = ko.observable();
	self.newPersonAge = ko.observable();
	self.newPersonAddress = ko.observable();
	
	self.addPerson = function() {
		// self.people.push();

		$.ajax({
			type : "POST",
			url : "/service/person/create",
			contentType: "application/json",
			data : JSON.stringify({
				name : this.newPersonName(),
				age : this.newPersonAge(),
				id : -1,
				address : this.newPersonAddress()
				}),
			success: function(data){
				self.refreshData();
			}
		});

//		$.post("/service/person/create", {
//			name : this.newPersonName(),
//			age : this.newPersonAge(),
//			id : -1,
//			address : this.newPersonAddress()
//		});
		self.newPersonName("");
		self.newPersonAge("");
		self.newPersonAddress("")
		self.refreshData();
	};

	self.removePerson = function(p) {
		var deleteApi = "/service/person/delete/" + p.id; 
		$.ajax({
			type : "DELETE",
			url  : deleteApi,
			contentType: "application/json"
		})
		.complete(function(data){
			alert("deleted user successfully");
		});
		self.people.remove(p);
	};

	self.refreshData = function() {
		// self.people = ko.observableArray([]);
		$.getJSON("/service/person/find/all", function(data) {
			var mappedPerson = $.map(data, function(p) {
				return new Person(p);
			});
			self.people(mappedPerson);
		}).error(function(){
			alert("No records found! Create some records");
		});
	};
}

$(document).ready(function() {
	ko.applyBindings(new PersonViewModel());
});
