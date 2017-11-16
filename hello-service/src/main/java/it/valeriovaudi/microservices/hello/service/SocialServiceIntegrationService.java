package it.valeriovaudi.microservices.hello.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import it.valeriovaudi.microservices.hello.anticorruption.TweetAnticorruptionLayer;
import it.valeriovaudi.microservices.hello.command.Rx1SocialServiceIntegrationServiceCommand;
import it.valeriovaudi.microservices.hello.config.ServiceConfiguration;
import it.valeriovaudi.microservices.hello.model.Tweet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by mrflick72 on 19/10/17.
 */


@Slf4j
@Service
@EnableConfigurationProperties(ServiceConfiguration.class)
public class SocialServiceIntegrationService {

    private final RestTemplate restTemplate;
    private final ServiceConfiguration serviceConfiguration;
    private final TweetAnticorruptionLayer tweetAnticorruptionLayer;
    private final ParamService paramService;

    public SocialServiceIntegrationService(RestTemplate restTemplate,
                                           ServiceConfiguration serviceConfiguration,
                                           TweetAnticorruptionLayer tweetAnticorruptionLayer,
                                           ParamService paramService) {
        this.restTemplate = restTemplate;
        this.serviceConfiguration = serviceConfiguration;
        this.tweetAnticorruptionLayer = tweetAnticorruptionLayer;
        this.paramService = paramService;
    }

    @HystrixCommand(commandProperties = {@HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")}, fallbackMethod = "getweetsDataFallback")
    public List<Tweet> getweetsData(String userName) {
        try{
            String uriString = UriComponentsBuilder.fromHttpUrl(serviceConfiguration.getTweetSocialServiceServiceTemplate())
                    .buildAndExpand(paramService.pram(userName)).toUriString();
            String body = restTemplate.exchange(uriString, HttpMethod.GET, HttpEntity.EMPTY, String.class).getBody();
            ArrayNode nodes = tweetAnticorruptionLayer.newTweetList(body);
            log.info("after ArrayNode nodes ");
            Iterable<JsonNode> iterable = nodes::iterator;
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(tweetAnticorruptionLayer::newTweet)
                    .collect(Collectors.toList());
        } catch (Throwable e){
            log.error(e.getMessage(), e);
            throw  new RuntimeException(e);
        }
    }

    public List<Tweet> getweetsDataFallback(String userName) {
        return new ArrayList<>();
    }

    public rx.Observable<Tweet> getTweetsDataObs(String userName) {
        return new Rx1SocialServiceIntegrationServiceCommand(restTemplate, serviceConfiguration, tweetAnticorruptionLayer, paramService, userName).observe();
    }

}

