package it.valeriovaudi.microservices.hello.endpoint;

import it.valeriovaudi.microservices.hello.aspect.Audit;
import it.valeriovaudi.microservices.hello.model.HelloMessage;
import it.valeriovaudi.microservices.hello.service.HelloService;
import it.valeriovaudi.microservices.hello.service.SocialServiceIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mrflick72 on 19/10/17.
 */

@RestController
public class HelloEndPoint {

    private final SocialServiceIntegrationService socialServiceIntegrationService;

    private final HelloService helloService;

    HelloEndPoint(SocialServiceIntegrationService socialServiceIntegrationService, HelloService helloService) {
        this.socialServiceIntegrationService = socialServiceIntegrationService;
        this.helloService = helloService;
    }

    @Audit
    @GetMapping("/hello/{userName}")
    public ResponseEntity getHello(@PathVariable("userName") String userName){
        final HelloMessage helloMessageResult = helloService.sayHello(userName).blockingGet();

        return ResponseEntity.ok(helloMessageResult);
    }

    @Audit
    @GetMapping("/withObservable/hello/{userName}")
    public ResponseEntity getHelloList(@PathVariable("userName") String userName) {
        return ResponseEntity.ok(socialServiceIntegrationService.getTweetsDataObs(userName).toList().toBlocking().single());
    }
}
