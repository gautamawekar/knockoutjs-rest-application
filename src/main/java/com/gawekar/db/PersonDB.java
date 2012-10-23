package com.gawekar.db;

import java.util.HashSet;
import java.util.Set;

import com.gawekar.model.Person;

public class PersonDB {
	private static final PersonDB db = new PersonDB();
	private int ID_GENERATOR = 0;

	private final Set<Person> storage = new HashSet<Person>();

	private PersonDB() {

	}

	public static PersonDB getInstance() {
		return db;
	}

	public void add(Person person) {
		Person copy = new Person(person);
		copy.setId(nextId());
		System.out.println(">>adding person: " + copy);
		this.storage.add(copy);
	}

	public void remove(Person person) {
		this.storage.remove(person);
	}

	public void removePersonById(int id) {
		this.storage.remove(findById(id));
	}

	public Set<Person> findByAge(int age) {
		Set<Person> personByAgeSet = new HashSet<Person>();
		for (Person person : storage) {
			if (person.getAge() == age) {
				personByAgeSet.add(person);
			}
		}
		return personByAgeSet;
	}

	public Set<Person> findByName(String name) {
		Set<Person> personByNameSet = new HashSet<Person>();
		for (Person person : storage) {
			if (person.getName().indexOf(name) != -1) {
				personByNameSet.add(person);
			}
		}
		return personByNameSet;
	}

	public Set<Person> findAll() {
		return this.storage;
	}

	public boolean exists(String name, int age) {
		Person p = new Person();
		p.setAge(age);
		p.setName(name);
		return storage.contains(p);
	}

	public boolean exists(int id) {
		Person p = new Person();
		p.setId(id);
		return storage.contains(p);
	}

	private Person findById(int id) {
		for (Person person : storage) {
			if (person.getId() == id) {
				return person;
			}
		}
		return null;
	}

	private int nextId() {
		ID_GENERATOR++;
		return ID_GENERATOR;
	}
}
