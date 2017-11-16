package it.valeriovaudi.microservices.hello.command;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import it.valeriovaudi.microservices.hello.anticorruption.TweetAnticorruptionLayer;
import it.valeriovaudi.microservices.hello.config.ServiceConfiguration;
import it.valeriovaudi.microservices.hello.model.Tweet;
import it.valeriovaudi.microservices.hello.service.ParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;

/**
 * Created by mrflick72 on 19/10/17.
 */

@Slf4j
public class Rx1SocialServiceIntegrationServiceCommand extends HystrixObservableCommand<Tweet> {

    final RestTemplate restTemplate;
    final ServiceConfiguration serviceConfiguration;
    final TweetAnticorruptionLayer tweetAnticorruptionLayer;
    final ParamService paramService;
    final String userName;

    public Rx1SocialServiceIntegrationServiceCommand(RestTemplate restTemplate, ServiceConfiguration serviceConfiguration, TweetAnticorruptionLayer tweetAnticorruptionLayer, ParamService paramService, String userName) {
        super(HystrixCommandGroupKey.Factory.asKey("rx1SocialServiceIntegrationServiceCommand"));
        this.restTemplate = restTemplate;
        this.serviceConfiguration = serviceConfiguration;
        this.tweetAnticorruptionLayer = tweetAnticorruptionLayer;
        this.paramService = paramService;
        this.userName = userName;
    }


    @Override
    protected Observable<Tweet> construct() {
        return Observable.create(emitter -> {
            log.info("start getTweetsData service: " + Thread.currentThread().getId());

            try{
                String uriString = UriComponentsBuilder.fromHttpUrl(serviceConfiguration.getTweetSocialServiceServiceTemplate())
                        .buildAndExpand(paramService.pram(userName)).toUriString();
                String body = restTemplate.exchange(uriString, HttpMethod.GET, HttpEntity.EMPTY, String.class).getBody();
                ArrayNode nodes = tweetAnticorruptionLayer.newTweetList(body);
                log.info("after ArrayNode nodes ");

                nodes.forEach(itemAux -> {
                    final Tweet tweet = tweetAnticorruptionLayer.newTweet(itemAux);
                    emitter.onNext(tweet);
                });

                emitter.onCompleted();
            } catch (Exception e){
                emitter.onError(e);
            }

            log.info("end getTweetsData service: " + Thread.currentThread().getId());
        });
    }

    @Override
    protected Observable resumeWithFallback(){
        return Observable.empty();
    }
}
