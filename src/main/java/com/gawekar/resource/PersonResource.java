package com.gawekar.resource;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.gawekar.db.PersonDB;
import com.gawekar.model.Person;

/**
 * Example resource class hosted at the URI path "/service/person"
 */
@Path("/person")
public class PersonResource {

	/**
	 * http://localhost:8080/webresources/person/sayHello/gautam
	 * 
	 * Method processing HTTP GET requests, producing "text/plain" MIME media
	 * type.
	 * 
	 * @return String that will be send back as a response of type "text/plain".
	 */
	@Path("/ping/{value}")
	@GET
	@Produces("text/plain")
	public String sayHello(@PathParam("value") String value) {
		return "Hi there! : " + value;
	}

	@Path("/find/age/{age}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response findByAge(@PathParam("age") int age) {
		Set<Person> pSet = db().findByAge(age);
		if (pSet.isEmpty()) {
			return Response.status(404)
					.entity("No persons found with age: " + age).build();
		}
		return Response.ok(pSet).build();
	}

	@Path("/find/name/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response findByName(@PathParam("name") String name) {
		Set<Person> pSet = db().findByName(name);
		if (pSet.isEmpty()) {
			return Response.status(404)
					.entity("No resources found with name: " + name).build();
		}
		return Response.ok(pSet).build();
	}

	@Path("/find/all")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response findAll() {
		Set<Person> pSet = db().findAll();
		if (pSet.isEmpty()) {
			return Response.status(404).entity("Found no resources").build();
		}
		return Response.ok(pSet).build();
	}

	/**
	 * Content-Type: application/json
	 * http://localhost:8080/webresources/person/create
	 * {"address":"some address","name":"some name","age":32}
	 * 
	 * 
	 * @param person
	 * @return
	 */

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Response createPerson(Person person) {
		if (db().exists(person.getName(), person.getAge())) {
			return Response.status(Status.CONFLICT)
					.entity("Person with same age/name exists").build();
		}
		db().add(person);
		return Response.ok(person).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/update")
	public Response updatePerson(Person person) {
		if (!db().exists(person.getId())) {
			return Response.status(Status.NOT_FOUND)
					.tag("Peron with requested id not found").build();
		}
		db().add(person);
		return Response.status(Status.NOT_FOUND)
				.entity("Requested person not found").build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/delete/{id}")
	public Response deletePerson(@PathParam("id") int id) {
		if (db().exists(id)) {
			db().removePersonById(id);
			System.out.println("successfully deleted user with id: " + id);
			return Response.ok("Resource deleted").build();
		}
		return Response.status(Status.NOT_FOUND)
				.entity("Requested person not found").build();
	}

	private PersonDB db() {
		return PersonDB.getInstance();
	}
}
