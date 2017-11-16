package it.valeriovaudi.microservices.hello.service;

import io.reactivex.Single;
import io.reactivex.schedulers  .Schedulers;
import it.valeriovaudi.microservices.hello.model.HelloMessage;
import it.valeriovaudi.microservices.hello.model.Tweet;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mrflick72 on 19/10/17.
 */

@Service
public class HelloService {

    private final PeopleServiceIntegrationService peopleServiceIntegrationService;
    private final SocialServiceIntegrationService socialServiceIntegrationService;
    private final InstanceService instanceService;

    public HelloService(PeopleServiceIntegrationService peopleServiceIntegrationService,
                        SocialServiceIntegrationService socialServiceIntegrationService,
                        InstanceService instanceService) {
        this.peopleServiceIntegrationService = peopleServiceIntegrationService;
        this.socialServiceIntegrationService = socialServiceIntegrationService;
        this.instanceService = instanceService;
    }


    public Single<HelloMessage> sayHello(String userName){
        Single<List<Tweet>> tweetsData = Single.<List<Tweet>>create(emitter ->
            emitter.onSuccess(socialServiceIntegrationService.getweetsData(userName)))
                .subscribeOn(Schedulers.io());

        Single<HelloMessage> peopleData = Single.<HelloMessage>create(emitter ->
                emitter.onSuccess(peopleServiceIntegrationService.getPeopleData(userName)))
                .subscribeOn(Schedulers.io());

        Single<String> instance = Single.just(instanceService.getInstance()).subscribeOn(Schedulers.io());

        return Single.zip(tweetsData, peopleData, instance, (tweetsList, helloMessage, instanceAux) -> {
            helloMessage.setInstance(instanceAux);helloMessage.setTweetList(tweetsList);return helloMessage;});
    }
}