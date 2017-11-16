package it.valeriovaudi.microservices.hello.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import it.valeriovaudi.microservices.hello.anticorruption.HelloMessageAnticorruptionLayer;
import it.valeriovaudi.microservices.hello.config.ServiceConfiguration;
import it.valeriovaudi.microservices.hello.model.HelloMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by mrflick72 on 19/10/17.
 */

@Slf4j
@Service
@EnableConfigurationProperties(ServiceConfiguration.class)
public class PeopleServiceIntegrationService {

    final RestTemplate restTemplate;
    final ServiceConfiguration serviceConfiguration;
    final HelloMessageAnticorruptionLayer messageAnticorruptionLayer;
    final ParamService paramService;

    public PeopleServiceIntegrationService(RestTemplate restTemplate,
                                           ServiceConfiguration serviceConfiguration,
                                           HelloMessageAnticorruptionLayer messageAnticorruptionLayer,
                                           ParamService paramService) {
        this.restTemplate = restTemplate;
        this.serviceConfiguration = serviceConfiguration;
        this.messageAnticorruptionLayer = messageAnticorruptionLayer;
        this.paramService = paramService;
    }

    @HystrixCommand(commandProperties = {@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")}, fallbackMethod = "fallback")
    public HelloMessage getPeopleData(String userName) {
        log.info("start PeopleServiceIntegrationService service: " + Thread.currentThread().getId());
        String uriString = UriComponentsBuilder.fromHttpUrl(serviceConfiguration.getPeopleServiceServiceTemplate())
                .buildAndExpand(paramService.pram(userName)).toUriString();
        String body = restTemplate.exchange(uriString, HttpMethod.GET, HttpEntity.EMPTY, String.class).getBody();
        try{
            final HelloMessage helloMessage = messageAnticorruptionLayer.newHelloMessage(body);
            log.info("end PeopleServiceIntegrationService service: " + Thread.currentThread().getId());

            return helloMessage;
        } catch (Exception e) {

            log.info("PeopleServiceIntegrationService get an exception");

            log.error(e.getMessage(), e);
            throw  new RuntimeException(e);
        }
    }


    public HelloMessage fallback(String userName){
        log.info("PeopleServiceIntegrationService get in fallback");
        return new HelloMessage();
    }
}