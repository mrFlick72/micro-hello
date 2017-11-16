package it.valeriovaudi.microservices.social;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableCaching
@EnableHystrix
@EnableEurekaClient
@SpringBootApplication
public class SocialServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialServicesApplication.class, args);
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}
}

@Data
@ConfigurationProperties(prefix = "service.configuration")
class ServiceConfiguration {
	private Integer maxTweets;
}


@RestController
@RequestMapping("/social")
class SocialRestFullEndPoint {

	final TwitterServices twitterServices;
	final ObservableTwitterServices observableTwitterServices;

	SocialRestFullEndPoint(TwitterServices twitterServices, ObservableTwitterServices observableTwitterServices) {
		this.twitterServices = twitterServices;
		this.observableTwitterServices = observableTwitterServices;
	}

	@GetMapping("/tweets/{userName}")
	public ResponseEntity tweets(@PathVariable("userName") String userName) {
		return ResponseEntity.ok(twitterServices.getTweets(userName));
	}

	@GetMapping("/observable/tweets/{userName}")
	public ResponseEntity observableTweets(@PathVariable("userName") String userName){
		return ResponseEntity.ok(observableTwitterServices.getObservableTweets(userName).toList().blockingGet());
	}
}

@Slf4j
@Component
class Expiration {

	@CacheEvict(allEntries = true, cacheNames = "getTweets")
	@Scheduled(fixedDelay = 10 * 60 * 1000 ,  initialDelay = 500)
	public void reportCacheEvict() {
		log.info("Expiration doGetTweets");
	}
}