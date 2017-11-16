package it.valeriovaudi.microservices.hello;

import io.reactivex.Observable;
import org.junit.Test;

import java.util.List;


public class HelloServiceApplicationTests {


	@Test
	public void contextLoads() {
		List<Object> objects = Observable.empty().toList().blockingGet();
		System.out.println(objects);
	}

}
