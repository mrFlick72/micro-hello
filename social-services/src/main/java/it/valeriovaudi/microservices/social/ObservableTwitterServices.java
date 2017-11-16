package it.valeriovaudi.microservices.social;

import io.reactivex.Observable;
import org.springframework.stereotype.Service;


@Service
public class ObservableTwitterServices {

    private final TwitterServices twitterServices;

    public ObservableTwitterServices(TwitterServices twitterServices) {
        this.twitterServices = twitterServices;
    }

    public Observable getObservableTweets(String userName){
        return Observable.create(observableEmitter -> {
            twitterServices.getTweets(userName).forEach(observableEmitter::onNext);
            observableEmitter.onComplete();
        });
    }

}
