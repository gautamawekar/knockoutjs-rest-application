package com.gawekar.resource;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.gawekar.model.Person;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PersonRestClient {
	private static final Random random = new Random();
	private final Client restClient = Client.create();

	PersonRestClient() {

	}

	public static void main(String args[]) throws Exception {
		PersonRestClient peopleService = new PersonRestClient();
		{
			System.out.println("Creating people");
			peopleService.createPerson("Gautam");
			peopleService.createPerson("Abhijeet");
		}
		int findAllCount = -1;
		List<Person> allPeople = new ArrayList<Person>();
		{
			System.out.println("Finding all>>");
			allPeople = peopleService.findAll();
			System.out.println("Total People count: " + allPeople.size());
			findAllCount = allPeople.size();
			for (Person p : allPeople) {
				System.out.println(p);
			}
		}

		{
			// delete first person
			peopleService.deletePerson(allPeople.get(0));
			System.out.println("Finding all>> again");
			allPeople = peopleService.findAll();
			System.out.println("Total People count: " + allPeople.size());
			if (allPeople.size() == findAllCount) {
				throw new Exception("hey user NOT deleted!");
			}
		}

		{
			System.out.println("Finding person by name");
			List<Person> people = peopleService.findbyName("Abhijeet");
			System.out.println("Total People count: " + people.size());
			for (Person p : people) {
				System.out.println(p);
			}
		}

	}

	private void deletePerson(Person p) throws Exception {
		WebResource webResource = restClient
				.resource("http://localhost:8080/service/person/delete/"
						+ p.getId());
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
		if (response.getStatus() == 200) {
			System.out.println("Person deleted succesfully");
			return;
		}
		throw new Exception("Unable to delete person. error: "
				+ IOUtils.toString(response.getEntityInputStream()));

	}

	private List<Person> findbyName(String name) throws Exception {
		WebResource webResource = restClient
				.resource("http://localhost:8080/service/person/find/name/"
						+ name);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		if (response.getStatus() == 200) {
			return retrievePeople(response.getEntityInputStream());
		}

		throw new Exception("Found no user with name: " + name + " error: "
				+ IOUtils.toString(response.getEntityInputStream()));

	}

	private List<Person> findAll() throws Exception {
		WebResource webResource = restClient
				.resource("http://localhost:8080/service/person/find/all");
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		if (response.getStatus() == 200) {
			return retrievePeople(response.getEntityInputStream());
		}

		String responseStr = IOUtils.toString(response.getEntityInputStream());
		throw new Exception("Found no user: " + responseStr);
	}

	private void createPerson(String personName) throws Exception {
		Person p = createRandomPersonWithName(personName);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter personJson = new StringWriter();
		mapper.writeValue(personJson, p);
		ClientResponse response = restClient
				.resource("http://localhost:8080/service/person/create")
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, personJson.toString());
		String responseStr = IOUtils.toString(response.getEntityInputStream());

		if (response.getStatus() == 200) {
			System.out.println("user added sucecsfully: " + responseStr);
		} else {
			System.out.println("FAILED to add user: " + responseStr);
		}
	}

	private Person createRandomPersonWithName(String name) {
		Person p = new Person();
		int nextRandomNos = random.nextInt(100);
		p.setName(name + nextRandomNos);
		p.setAge(nextRandomNos);
		p.setAddress("some address");
		return p;
	}

	private List<Person> retrievePeople(InputStream is) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(is, new TypeReference<List<Person>>() {
		});

	}
}
