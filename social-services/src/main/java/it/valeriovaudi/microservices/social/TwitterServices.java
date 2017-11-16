package it.valeriovaudi.microservices.social;


import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@EnableConfigurationProperties(ServiceConfiguration.class)
public class TwitterServices {

    private final Twitter twitterTemplate;
    private final ServiceConfiguration serviceConfiguration;

    public TwitterServices(Twitter twitterTemplate, ServiceConfiguration serviceConfiguration) {
        this.twitterTemplate = twitterTemplate;
        this.serviceConfiguration = serviceConfiguration;
    }


    public Observable getObservableTweets(String userName){
        return Observable.create(observableEmitter -> {
            getTweets(userName).forEach(observableEmitter::onNext);
            observableEmitter.onComplete();
        });
    }

    @Cacheable("getTweets")
    public List<Tweet> getTweets(String userName){
        log.info("doGetTweets");
        return twitterTemplate.searchOperations()
                .search(userName, serviceConfiguration.getMaxTweets()).getTweets();
    }
}
