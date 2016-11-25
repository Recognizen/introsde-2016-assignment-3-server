package introsde.assignment.soap.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFormat;

import introsde.assignment.soap.dao.LifeCoachDao;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the "Person" database table.
 * 
 */
@Entity
@Table(name="\"Person\"")
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@XmlType(propOrder = { "id", "firstname", "lastname", "birthdate", "email", "username", "measure" })
@XmlRootElement
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	// For sqlite in particular, you need to use the following @GeneratedValue annotation
	// This holds also for the other tables
	// SQLITE implements auto increment ids through named sequences that are stored in a 
	// special table named "sqlite_sequence"
	@GeneratedValue(generator="sqlite_person")
	@TableGenerator(name="sqlite_person", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="Person")
	@Column(name="id")
	private long id;

	@Column(name="firstname")
	private String firstname;
	
	@Column(name="lastname")
	private String lastname;

	@Temporal(TemporalType.DATE)
	@Column(name="birthdate")
	private Date birthdate;
	
	@Column(name="email")
	private String email;

	@Column(name="username")
	private String username;
	
	// mappedBy must be equal to the name of the attribute in Measure that maps this relation
	@OneToMany(mappedBy="person",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private List<Measure> currentHealth;

	@XmlTransient
	@OneToMany(mappedBy="person",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private List<Measure> healthHistory;
	
	public Person() {
	}
	
	public Date getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String name) {
		this.firstname = name;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElementWrapper(name = "currentHealth")
	public List<Measure> getMeasure() {
	    return currentHealth;
	}

	public void setMeasure(List<Measure> param) {
	    this.currentHealth = param;
	}
	/*
	@XmlTransient
//	@XmlElementWrapper(name = "healthHistory")
	public List<Measure> getHistory() {
	    return healthHistory;
	}

	public void setHistory(List<Measure> param) {
	    this.healthHistory = param;
	}
	*/
	// Database operations
	// Notice that, for this example, we create and destroy and entityManager on each operation. 
	// How would you change the DAO to not having to create the entity manager every time? 
	public static Person getPersonById(long personId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Person p = em.find(Person.class, personId);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}
	
	public static List<Person> getAll() {

		System.out.println("--> Initializing Entity manager...");
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		System.out.println("--> Querying the database for all the people...");
	    List<Person> list = em.createNamedQuery("Person.findAll", Person.class).getResultList();
		System.out.println("--> Closing connections of entity manager...");
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	// add other database global access operations

	public static Person savePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static Person updatePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p=em.merge(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static void removePerson(Person p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	}
}
