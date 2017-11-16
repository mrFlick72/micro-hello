package it.valeriovaudi.microservices.people;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

@EnableCaching
@EnableHystrix
@EnableEurekaClient
@SpringBootApplication
public class PeopleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeopleServiceApplication.class, args);
	}


	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}
}

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userName")
class Person implements Serializable {

	@Id
	private String userName;

	@Column(unique = true)
	private String mail;

	private String firstName;
	private String lastName;
}

interface PeopleRepository extends JpaRepository<Person, String> {

	@Cacheable("person")
	@Transactional(readOnly = true)
	Person findByUserName(String userName);

	@Override
	@Cacheable("personStream")
	@Transactional(readOnly = true)
	List<Person> findAll();

	@Override
	@Caching(evict = @CacheEvict(cacheNames = {"personStream"}, allEntries = true),
			 put = @CachePut(cacheNames = {"person"}))
	Person save(Person person);

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames = {"person"}),
			@CacheEvict(cacheNames = {"personStream"}, allEntries = true)})
	void delete(String userName);

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames = {"person"}),
			@CacheEvict(cacheNames = {"personStream"}, allEntries = true)})
	void delete(Person person);
}


@Service
class PersonService {

	final PeopleRepository peopleRepository;

	PersonService(PeopleRepository peopleRepository) {
		this.peopleRepository = peopleRepository;
	}

	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	List<Person> findAll(){
		return peopleRepository.findAll();
	}

	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	Person findByUserName(String userName){
		return peopleRepository.findByUserName(userName);
	}

	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	void deletePerson(String userName) {
		peopleRepository.delete(userName);
	}

	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	Person newPerson(Person person){
		return peopleRepository.save(person);
	}

	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	Person updatePerson(Person person){
		return peopleRepository.save(person);
	}
}


@RestController
@RequestMapping("/person")
class PersonRestFullEndPoint {

	final PersonService personService;

	PersonRestFullEndPoint(PersonService personService) {
		this.personService = personService;
	}

	@GetMapping
	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	ResponseEntity<List<Person>> findAll(){
		return ResponseEntity.ok(personService.findAll());
	}

	@GetMapping("/{userName}")
	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	ResponseEntity<Person> findByUserName(@PathVariable("userName") String userName){
		return ResponseEntity.ok(personService.findByUserName(userName));
	}

	@PostMapping
	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	ResponseEntity newPerson(@RequestBody Person person){
		Person personAux = personService.newPerson(person);
		URI findByUserName = MvcUriComponentsBuilder.fromMethodName(PersonRestFullEndPoint.class, "findByUserName",
				personAux.getUserName()).build().toUri();
		return ResponseEntity.created(findByUserName).build();
	}

	@PutMapping("/{userName}")
	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	ResponseEntity updatePerson(@PathVariable("userName") String userName, @RequestBody Person person){
		person.setUserName(userName);
		personService.updatePerson(person);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{userName}")
	@HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	ResponseEntity deletePerson(@PathVariable("userName") String userName){
		personService.deletePerson(userName);
		return ResponseEntity.noContent().build();
	}

}

@Component
class InitDataBase implements CommandLineRunner {

	final PeopleRepository peopleRepository;

	InitDataBase(PeopleRepository peopleRepository) {
		this.peopleRepository = peopleRepository;
	}

	@Override
	public void run(String... strings) {
		Person person = new Person("mrFlick72", "valerio.vaudi@sample.com", "Valerio", "Vaudi");
		peopleRepository.save(person);
	}
}